package benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import streams._

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@State(Scope.Thread)
@Fork(1)
class ReferencePush {
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

  @Benchmark
  def sum () : Long = {
    val sum : Long = Push.of(v)
      .fold(0L)(_+_)
    sum
  }

  @Benchmark
  def sumOfSquares () : Long = {
    val sum : Long = Push.of(v)
      .map(d => d * d)
      .fold(0L)(_+_)
    sum
  }

  @Benchmark
  def sumOfSquaresEven () : Long = {
    val res : Long = Push.of(v)
      .filter(x => x % 2L == 0L)
      .map(x => x * x)
      .fold(0L)(_+_)
    res
  }

  @Benchmark
  def maps () : Long = {
    val res : Long = Push.of(v)
      .map(x => x * 1)
      .map(x => x * 2)
      .map(x => x * 3)
      .map(x => x * 4)
      .map(x => x * 5)
      .map(x => x * 6)
      .map(x => x * 7)
      .fold(0L)(_+_)
    res
  }

  @Benchmark
  def filters () : Long = {
    val res : Long = Push.of(v)
      .filter(x => x > 1)
      .filter(x => x > 2)
      .filter(x => x > 3)
      .filter(x => x > 4)
      .filter(x => x > 5)
      .filter(x => x > 6)
      .filter(x => x > 7)
      .fold(0L)(_+_)
    res
  }

  @Benchmark
  def cart () : Long = {
    val sum : Long = Push.of(vHi)
      .flatMap(d => Push.of(vLo).map (dp => dp * d))
      .fold(0L)(_+_)
    sum
  }

  @Benchmark
  def flatMap_take () : Long = {
    val sum = Push.of(v).flatMap((x) => Push.of(vLo).map((dP) => dP * x)).take(20000000).fold(0L)(_+_)
    sum
  }
}