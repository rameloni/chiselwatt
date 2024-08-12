import chisel3._
import chisel3.util.{MuxCase, MuxLookup}
import circt.stage.ChiselStage

import Control._
import Control.InternalOps._
import Helpers._

class Logical(bits: Int) extends Module {
    val io         = IO(new Bundle {
    val a          = Input(UInt(bits.W))
    val b          = Input(UInt(bits.W))
    val internalOp = Input(Control.InternalOps())
    val invertIn   = Input(UInt(1.W))
    val invertOut  = Input(UInt(1.W))
    val length     = Input(UInt(2.W))
    val out        = Output(UInt(bits.W))
  })

  val b = Mux(io.invertIn.asBool, ~io.b, io.b)

  val ext = MuxLookup(io.length, io.a.signExtend(8, bits))(Array(
              LEN_2B -> io.a.signExtend(16, bits),
              LEN_4B -> io.a.signExtend(32, bits)))

  val tmp = MuxCase(io.a, Seq(
      (io.internalOp === LOG_AND) -> (io.a & b),
      (io.internalOp === LOG_OR) -> (io.a | b),
      (io.internalOp === LOG_XOR) -> (io.a ^ b),
      (io.internalOp === LOG_EXTS) -> ext
      ))

  io.out := Mux(io.invertOut.asBool, ~tmp, tmp)
}

object LogicalObj extends App {
  ChiselStage.emitSystemVerilog(new Logical(64))
}
