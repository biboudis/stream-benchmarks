package benchmarks

import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@State(Scope.Thread)
@Fork(1)
class Baseline {
  var N: Int = _
  var v: Array[Long] = _
  var vHi: Array[Long] = _
  var vLo: Array[Long] = _

  @Setup
  def prepare(): Unit = {
    N = 10000000
    v = Array.tabulate(N)(i => i.toLong % 1000)
    vHi = Array.tabulate(1000000)(_.toLong)
    vLo = Array.tabulate(10)(_.toLong)
  }

  // Baselines
  @Benchmark
  def sum(): Long = {
    var i = 0
    var sum = 0L
    while (i < v.length) {
      sum += v(i)
      i += 1
    }
    sum
  }

  @Benchmark
  def sumOfSquares(): Long = {
    var i = 0
    var sum = 0L
    while (i < v.length) {
      sum += v(i) * v(i)
      i += 1
    }
    sum
  }

  @Benchmark
  def sumOfSquaresEven(): Long = {
    var i = 0
    var sum = 0L
    while (i < v.length) {
      if (v(i) % 2L == 0L)
        sum += v(i) * v(i)
      i += 1
    }
    sum
  }

  @Benchmark
  def filters () : Long = {
    var i=0
    var sum=0L
    while (i < v.length) {
      if (v(i) > 1 && v(i) > 2 && v(i) > 3 && v(i) > 4 && v(i) > 5 && v(i) > 6 && v(i) > 7)
        sum += v(i)
      i += 1
    }
    sum
  }

  @Benchmark
  def maps () : Long = {
    var i=0
    var sum=0L
    while (i < v.length) {
      sum += v(i) * 1*2*3*4*5*6*7
      i += 1
    }
    sum
  }

  @Benchmark
  def cart(): Long = {
    var d, dp = 0
    var sum = 0L
    while (d < vHi.length) {
      dp = 0
      while (dp < vLo.length) {
        sum += vHi(d) * vLo(dp)
        dp += 1
      }
      d += 1
    }
    sum
  }

  @Benchmark
  def flatMap_take () : Long = {
    var counter1 = 0
    var counter2 = 0
    var sum = 0L
    var n = 0
    var flag = true
    val size1 = v.length
    val size2 = vLo.length
    while (counter1 < size1 && flag) {
      val item1 = v(counter1)
      while (counter2 < size2 && flag) {
        val item2 = vLo(counter2)
        sum = sum + item1 * item2
        counter2 += 1
        n += 1
        if (n == 20000000)
          flag = false
      }
      counter2 = 0
      counter1 += 1
    }
    sum
  }
}