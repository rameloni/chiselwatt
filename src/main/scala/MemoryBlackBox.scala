import chisel3._
import chisel3.util.{HasBlackBoxInline, log2Ceil}
import circt.stage.ChiselStage
import chisel3.util.experimental.loadMemoryFromFile


class MemoryBlackBox(val bits: Int, val words: Int, val filename: String) extends Module {
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


  val ram = Mem(words, UInt(bits.W))
    loadMemoryFromFile(ram, filename)

//  for(i <- 0 until words) {
//    io.ramOut := ram(i)
//  }

  io.readData1 := ram(io.addr1)
  io.readData2 := ram(io.readAddr2)

  when(io.writeEnable1) {
    for (i <- 0 until bits/8) {
      when(io.writeMask1(i)) {
//        ram(io.addr1)(i * 8 + 7, i * 8) := io.writeData1(i * 8 + 7, i * 8)
        val (sIdx, eIdx) = (i * 8 + 7, i * 8)
        val newBits = io.writeData1(sIdx, eIdx)
        val oldBits = ram(io.addr1)

        val mask = ((1 << (sIdx - eIdx + 1)) - 1).U

        val bits = (oldBits & (~mask).asUInt) | (newBits << eIdx.U).asUInt


        ram(io.addr1) := bits
//        ram(io.addr1) := io.writeData1(i * 8 + 7, i * 8)
      }
    }
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
//  val ramOut = IO(Output(UInt((bits*words).W)))

  val mem = Mem(words, UInt(bits.W))
  loadMemoryFromFile(mem, filename)
  val m = Module(new MemoryBlackBox(bits, words, filename))

  m.io.clock := clock

  m.io.writeEnable1 := io.loadStorePort.writeEnable
  m.io.writeMask1 := io.loadStorePort.writeMask
  m.io.addr1 := io.loadStorePort.addr
  io.loadStorePort.readData := m.io.readData1
  m.io.writeData1 := io.loadStorePort.writeData

  m.io.readAddr2 := io.fetchPort.addr
  io.fetchPort.readData := m.io.readData2
//  ramOut <> m.io.ramOut
}

object MemoryBlackBoxObj extends App {
  println(ChiselStage.emitSystemVerilog(new MemoryBlackBoxWrapper(64, 1024, "test.hex"), firtoolOpts = Array("--preserve-aggregate", "all")))
  println(ChiselStage.emitCHIRRTL(new MemoryBlackBoxWrapper(64, 1024, "test.hex")))
}
