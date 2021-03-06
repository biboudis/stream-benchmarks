package benchmarks

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import strawman.collection._

@BenchmarkMode(scala.Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 30)
@Measurement(iterations = 30)
@State(Scope.Benchmark)
class StrawmanViews {

  @Param(scala.Array("2", "4", "8", "16", "39", "282", "73121", "7312102"))
  var sizeOuter: Int = _

  @Param(scala.Array("282"))
  var sizeInner: Int = _

  var shortRangingFactor: Int = _
  var vOuter: ArrayView[Long] = _
  var vInner: ArrayView[Long] = _

  @Setup(Level.Trial)
  def initData(): Unit = {

    def fillArray(range: Int) = {
      val array = new Array[Long](range)
      var i = 0
      while (i < range) {
        array(i) = scala.util.Random.nextInt(range).toLong
        i += 1
      }
      array
    }

    shortRangingFactor = (sizeOuter * 0.2).toInt
    vOuter = ArrayView(fillArray(sizeOuter))
    vInner = ArrayView(fillArray(sizeInner))
  }

  @Benchmark
  def fold(bh: Blackhole) : Unit = {
    val res : Long = vOuter
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }

  @Benchmark
  def fold_map(bh: Blackhole) : Unit = {
    val res : Long = vOuter
      .map(d => d * d)
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }

  @Benchmark
  def fold_map_filter(bh: Blackhole) : Unit = {
    val res : Long = vOuter
      .filter(x => x % 2L == 0L)
      .map(x => x * x)
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }

  @Benchmark
  def multiple_maps(bh: Blackhole) : Unit = {
    val res : Long = vOuter
      .map(x => x * 1)
      .map(x => x * 2)
      .map(x => x * 3)
      .map(x => x * 4)
      .map(x => x * 5)
      .map(x => x * 6)
      .map(x => x * 7)
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }

  @Benchmark
  def multiple_filters(bh: Blackhole) : Unit = {
    val res : Long = vOuter
      .filter(x => x > 1)
      .filter(x => x > 2)
      .filter(x => x > 3)
      .filter(x => x > 4)
      .filter(x => x > 5)
      .filter(x => x > 6)
      .filter(x => x > 7)
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }

  @Benchmark
  def flatMap_map_sum(bh: Blackhole) : Unit  = {
    val res : Long = vOuter
      .flatMap(d => vInner.map (dp => dp * d))
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }

  @Benchmark
  def flatMap_take(bh: Blackhole) : Unit = {
    val res : Long = vOuter
      .flatMap((x) => vInner
        .map((dP) => dP * x))
      .take(shortRangingFactor)
      .foldLeft(0L)(_+_)

    bh.consume(res)
  }
}