import chisel3._
import tywaves.simulator.TywavesSimulator._
import tywaves.simulator.simulatorSettings._

import org.scalatest.flatspec.AnyFlatSpec


class CoreTest extends AnyFlatSpec {
  val bits = 64
  val words = 16 * 1024
  val resetAddr = 0x0
  val filename =  System.getProperty("user.dir") + "/insns.hex"
  val frequency = 50000000

  behavior of "Core"
  it should "pass hello world test" in {
    simulate(new Core(bits, words, filename, resetAddr, frequency), Seq(VcdTrace, WithTywavesWaveforms(true), SaveWorkdirFile("workDir")))
      { c =>
        // Write the memory manually
        c.reset.poke(true.B)
        c.clock.step()
        c.reset.poke(false.B)
        c.clock.step(100)
        c.clock.step(1000)

    }
  }
}
