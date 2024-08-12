import chisel3._
import chisel3.util.{Cat, HasBlackBoxInline, Reverse, log2Ceil}
import circt.stage.ChiselStage
import chisel3.util.experimental.loadMemoryFromFile




class MemoryBlackBox(val bits: Int, val words: Int, val filename: String) extends Module {
  def readFileToSequence(filename: String): Seq[BigInt] = {
    val bufferedSource = scala.io.Source.fromFile(filename)
    try {
      val lines = bufferedSource.getLines()
//      println(lines.toSeq)
      lines.map(line => BigInt(line, 16)).toSeq
//      bufferedSource.getLines().toSeq
    } finally {
      bufferedSource.close()
    }
  }
  val io = IO(new Bundle() {
    val clock      = Input(Clock())

    val writeEnable1 = Input(Bool())
    val writeMask1   = Input(UInt((bits/8).W))
    val addr1        = Input(UInt(log2Ceil(words).W))
    val readData1    = Output(UInt(bits.W))
    val writeData1   = Input(UInt(bits.W))

    val readAddr2   = Input(UInt(log2Ceil(words).W))
    val readData2   = Output(UInt(bits.W))
//    val ramOut      = Output(UInt((bits*words).W))
  })

  object StateMem extends ChiselEnum {
    val EMPTY, INITIALIZED = Value
  }
  val state = RegInit(StateMem.EMPTY)

  val ram = Mem(words, UInt(bits.W))
  val memContent = readFileToSequence(filename)
  println(memContent)
//    loadMemoryFromFile(ram, filename)

  when(reset.asBool && state === StateMem.EMPTY) {
    if(memContent.nonEmpty) {
      // Read the file and initialize it
      val x = if (memContent.length < words) memContent.length else words
      for (i <- 0 until x) {
        ram(i) := memContent(i).asUInt
      }
    }
    state := StateMem.INITIALIZED
  }


  io.readData1 := ram(io.addr1)
  io.readData2 := ram(io.readAddr2)

  def ffo(pwidth:Int, in:UInt, newByte: UInt) : UInt = {
    val ary = Wire(Vec(pwidth, Bool()))
    ary(0) := in(0)
    for(w <- 1 until pwidth) {
      ary(w) := newByte(w-1)
    }
    val rval = Reverse(Cat(ary))
    rval
  }

  def replaceBits(a: UInt, b: UInt, mask: UInt) = {
    val a_cleared = a & (~mask).asUInt
    val b_extracted = b & mask
    a_cleared | b_extracted
  }
  when(io.writeEnable1) {
    val newVal = VecInit(ram(io.addr1).asBools)
    for (i <- 0 until bits/8) {
      when(io.writeMask1(i)) {
        val newByte = io.writeData1(i * 8 + 7, i * 8)
        for (j <- 0 until 8) {
          newVal(i * 8 + j) := newByte(j)
        }
      }
    }
    ram.write(io.addr1, newVal.asUInt)
  }


//  setInline("MemoryBlackBox.v",
//    s"""
//      |module MemoryBlackBox #(
//      |    parameter BITS = $bits,
//      |    parameter WORDS = $words
//      |) (
//      |    input clock,
//      |
//      |    input writeEnable1,
//      |    input [BITS/8-1:0] writeMask1,
//      |    input [$$clog2(WORDS)-1:0] addr1,
//      |    output reg [BITS-1:0] readData1,
//      |    input [BITS-1:0] writeData1,
//      |
//      |    input [$$clog2(WORDS)-1:0] readAddr2,
//      |    output reg [BITS-1:0] readData2,
//      |    output reg [WORDS-1:0][BITS-1:0] ramOut
//      |);
//      |
//      |integer i;
//      |reg [BITS-1:0] ram[0:WORDS-1];
//      |
//      |always@(posedge clock)
//      |begin
//      |    readData1 <= ram[addr1];
//      |    readData2 <= ram[readAddr2];
//      |    if (writeEnable1)
//      |    begin
//      |      for (i = 0; i < BITS/8; i = i + 1)
//      |      begin
//      |        if (writeMask1[i]) ram[addr1][i*8+:8] <= writeData1[i*8+:8];
//      |      end
//      |    end
//      |end
//      |initial begin
//      |    $$readmemh("$filename", ram);
//      |end
//      |
//      |always@(ram)
//      |begin
//      |   for (i=0;i<WORDS; i=i+1)
//      |   begin
//      |       ramOut[i] = ram[i];
//      |   end
//      |end
//      |
//      |endmodule
//      |""".stripMargin)
}

class MemoryPort(val bits: Int, val words: Int, val rw: Boolean) extends Bundle {
  val addr        = Output(UInt(log2Ceil(words).W))
  val readData    = Input(UInt(bits.W))

  //val writeEnable = if (rw) Some(Output(Bool())) else None
  //val writeMask   = if (rw) Some(Output(UInt((bits/8).W))) else None
  //val writeData   = if (rw) Some(Output(UInt(bits.W))) else None
  val writeEnable = (Output(Bool()))
  val writeMask   = (Output(UInt((bits/8).W)))
  val writeData   = (Output(UInt(bits.W)))
}

class MemoryBlackBoxWrapper(val bits: Int, val words: Int, val filename: String) extends Module {
  val io = IO(new Bundle() {
    val loadStorePort = Flipped(new MemoryPort(bits, words, true))
    val fetchPort = Flipped(new MemoryPort(bits, words, false))
  })

  val m = Module(new MemoryBlackBox(bits, words, filename))

  m.io.clock := clock

  m.io.writeEnable1 := io.loadStorePort.writeEnable
  m.io.writeMask1 := io.loadStorePort.writeMask
  m.io.addr1 := io.loadStorePort.addr
  io.loadStorePort.readData := m.io.readData1
  m.io.writeData1 := io.loadStorePort.writeData

  m.io.readAddr2 := io.fetchPort.addr
  io.fetchPort.readData := m.io.readData2
}

object MemoryBlackBoxObj extends App {
  println(ChiselStage.emitSystemVerilog(new MemoryBlackBoxWrapper(64, 1024, "test.hex"), firtoolOpts = Array("--preserve-aggregate", "all")))
  println(ChiselStage.emitCHIRRTL(new MemoryBlackBoxWrapper(64, 1024, "test.hex")))
}
