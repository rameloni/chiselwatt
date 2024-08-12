import chisel3._
import tywaves.simulator.simulatorSettings.VcdTrace
//import chiseltest._
//import chisel3.simulator.EphemeralSimulator._
import tywaves.simulator.TywavesSimulator._
import TestValues._
import org.scalatest.flatspec.AnyFlatSpec

class CountZeroesUnitTester extends AnyFlatSpec {
  behavior of "CountZeroes"
  it should "pass a unit test" in {
    simulate(new CountZeroes(64), Seq(VcdTrace)) { c =>

      def clz(x: BigInt): Int = {
        for (i <- 0 until 64) {
          if (((x >> (63 - i)) & 1) == 1) {
            return i
          }
        }
        return 64;
      }

      for (x <- testValues) {
        c.io.a.poke(x.asUInt)
        c.io.out.expect(clz(x).asUInt)
      }

      def clz32(x: BigInt): Int = {
        for (i <- 0 until 32) {
          if (((x >> (31 - i)) & 1) == 1) {
            return i
          }
        }
        return 32;
      }

      c.io.is32bit.poke(true.B)
      for (x <- testValues) {
        c.io.a.poke(x.asUInt)
        c.io.out.expect(clz32(x).asUInt)
      }
      c.io.is32bit.poke(false.B)

      def ctz(x: BigInt): Int = {
        for (i <- 0 until 64) {
          if (((x >> i) & 1) == 1) {
            return i
          }
        }
        return 64;
      }

      c.io.countRight.poke(true.B)
      for (x <- testValues) {
        c.io.a.poke(x.asUInt)
        c.io.out.expect(ctz(x).asUInt)
      }

      def ctz32(x: BigInt): Int = {
        for (i <- 0 until 32) {
          if (((x >> i) & 1) == 1) {
            return i
          }
        }
        return 32;
      }

      c.io.is32bit.poke(true.B)
      for (x <- testValues) {
        c.io.a.poke(x.asUInt)
        c.io.out.expect(ctz32(x).asUInt)
        c.clock.step()
      }
    }
  }
} 
