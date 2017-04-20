package benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import strawman.collection._

@OutputTimeUnit(TimeUnit.MILLISECONDS)
@BenchmarkMode(Array(Mode.AverageTime))
@State(Scope.Thread)
@Fork(1)
class Strawman {
  var N: Int = _
  var v: View[Long] = _
  var vHi: View[Long] = _
  var vLo: View[Long] = _

  @Setup
  def prepare(): Unit = {
    N = 10000000
    v = strawman.collection.ArrayView(Array.tabulate(N)(i => i.toLong % 1000))
    vHi = strawman.collection.ArrayView(Array.tabulate(1000000)(_.toLong))
    vLo = strawman.collection.ArrayView(Array.tabulate(10)(_.toLong))
  }

  @Benchmark
  def sum () : Long = {
    val sum : Long = v.view
      .foldLeft(0L)(_+_)
    sum
  }

  @Benchmark
  def sumOfSquares () : Long = {
    val sum : Long = v.view
      .map(d => d * d)
      .foldLeft(0L)(_+_)
    sum
  }

  @Benchmark
  def sumOfSquaresEven () : Long = {
    val res : Long = v.view
      .filter(x => x % 2L == 0L)
      .map(x => x * x)
      .foldLeft(0L)(_+_)
    res
  }

  @Benchmark
  def maps () : Long = {
    val res : Long = v.view
      .map(x => x * 1)
      .map(x => x * 2)
      .map(x => x * 3)
      .map(x => x * 4)
      .map(x => x * 5)
      .map(x => x * 6)
      .map(x => x * 7)
      .foldLeft(0L)(_+_)
    res
  }

  @Benchmark
  def filters () : Long = {
    val res : Long = v.view
      .filter(x => x > 1)
      .filter(x => x > 2)
      .filter(x => x > 3)
      .filter(x => x > 4)
      .filter(x => x > 5)
      .filter(x => x > 6)
      .filter(x => x > 7)
      .foldLeft(0L)(_+_)
    res
  }

  @Benchmark
  def cart () : Long = {
    val sum : Long = vHi.view
      .flatMap(d => vLo.view.map (dp => dp * d))
      .foldLeft(0L)(_+_)
    sum
  }

  @Benchmark
  def flatMap_take () : Long = {
    val sum = v.view.flatMap((x) => vLo.view.map((dP) => dP * x)).take(20000000).foldLeft(0L)(_+_)
    sum
  }
}