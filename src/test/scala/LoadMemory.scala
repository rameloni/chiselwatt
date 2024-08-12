import chisel3._
import tywaves.simulator.TywavesSimulator._
import tywaves.simulator.simulatorSettings._
//import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import chisel3.util.experimental.loadMemoryFromFileInline   // <<-- new import here
class UsesMem(memoryDepth: Int, memoryType: Data, filename: String) extends Module {
  val io = IO(new Bundle {
    val address = Input(UInt(memoryType.getWidth.W))
    val value   = Output(memoryType)
  })
  val memory = Mem(memoryDepth, memoryType)
  io.value := memory(io.address)

  loadMemoryFromFileInline(memory, filename)  // <<-- Note the annotation here
}
class LoadMemory extends AnyFlatSpec {
  val bits = 64
  val words = 4
  // File in the current path
  val filename = System.getProperty("user.dir") + "/MemoryInit.hex"
  val frequency = 50000000


  scala.reflect.io.File(filename).writeAll("0001020304050607\r\n08090A0B0C0D0E0F\r\n0F0E0D0C0B0A0908\r\n8080808080808080\r\n")

  behavior of "LoadMemory"
  it should "pass a unit test" in {
    simulate(new UsesMem(words, UInt(bits.W), filename), Seq(VcdTrace, WithTywavesWaveforms(true), WithFirtoolArgs(Seq("--disable-all-randomization")), SaveWorkdirFile("workDir")))
//      .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation))
      { m =>
        m.clock.step()
//        m.reset.poke(true.B)
//        m.clock.step()
//        m.reset.poke(false.B)
//        m.clock.step()
//        m.clock.step()
//        m.clock.step()
//        m.io.address.poke(0.U)
//        m.clock.step()
//        m.io.address.poke(2.U)
    }
  }
}
