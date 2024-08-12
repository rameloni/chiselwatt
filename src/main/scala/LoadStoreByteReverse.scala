import chisel3._
import chisel3.util.{MuxLookup}
import circt.stage.ChiselStage

import Control.LenEnum._
import Helpers._

class LoadStoreByteReverse(bits: Int) extends Module {
  val io = IO(
    new Bundle {
      val in     = Input(UInt(bits.W))
      val length = Input(Control.LenEnum())
      val out    = Output(UInt(bits.W))
    }
  )

  val lengths = Seq(2, 4, 8)
  val lengthNames = Seq(LEN_2B, LEN_4B, LEN_8B)

  val lookupTable = lengthNames.zip(
    lengths.map(l =>
      io.in.bytes().zipWithIndex.filter({case (b@_, i) => (i < l)}).map({case (b, i@_) => b}).reduce(_ ## _)
    )
  )
  
  io.out := MuxLookup(io.length, lookupTable.head._2)(lookupTable)
}

object LoadStoreByteReverseObj extends App {
  ChiselStage.emitSystemVerilog(new LoadStoreByteReverse(64))
}

object LoadStoreByteReverse {
  /** Return a [[UInt]] the number of leading zeroes in some [[Data]]
    * @param in a hardware type
    * @tparam A some data
    * @param length a hardware type
    * @tparam B some data
    */
  def apply[A <: Data, B <: Data](in: A, length: B): UInt = {
    val count = Module(new LoadStoreByteReverse(in.getWidth))
    count.io.in := in.asUInt
    count.io.length := length
    count.io.out
  }
}
