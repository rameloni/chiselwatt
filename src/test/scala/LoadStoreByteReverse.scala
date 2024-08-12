import chisel3._
import tywaves.simulator.simulatorSettings.VcdTrace
//import chiseltest._
import tywaves.simulator.TywavesSimulator._
import Control._
import org.scalatest.flatspec.AnyFlatSpec

class LoadStoreByteReverseTester extends AnyFlatSpec  {
  val x = BigInt("0123456789ABCDEF", 16)
  val bits = 64

  behavior of "LoadStoreByteReverse"
  it should "pass a unit test" in {
    simulate(new LoadStoreByteReverse(bits), Seq(VcdTrace)) { br =>
      br.io.in.poke(x.U)

      br.io.length.poke(LEN_2B)
      br.io.out.expect("h000000000000EFCD".U)

      br.io.length.poke(LEN_4B)
      br.io.out.expect("h00000000EFCDAB89".U)

      br.io.length.poke(LEN_8B)
      br.io.out.expect("hEFCDAB8967452301".U)
    }
  }
}
