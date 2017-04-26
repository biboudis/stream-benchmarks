package strawman.collection.immutable

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import org.openjdk.jmh.infra.Blackhole
import strawman.collection.{ArrayView, View}
import scala.{Any, AnyRef, Int, Long, Unit, Array}
import scala.Predef.intWrapper

@BenchmarkMode(scala.Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 30)
@Measurement(iterations = 30)
@State(Scope.Benchmark)
class ArrayBaseline {

  @Param(scala.Array("2", "4", "8", "16", "39", "282", "73121", "7312102"))
  var sizeOuter: Int = _

  @Param(scala.Array("2", "4", "8", "16", "39", "282", "73121", "7312102"))
  var sizeInner: Int = _

  var shortRangingFactor: Int = _
  var vOuter: Array[Long] = _
  var vInner: Array[Long] = _

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
    vOuter = fillArray(sizeOuter)
    vInner = fillArray(sizeInner)
  }

  @Benchmark
  def fold(bh: Blackhole): Unit = {
    var i = 0
    var ret = 0L
    while (i < vOuter.length) {
      ret += vOuter(i)
      i += 1
    }
    bh.consume(ret)
  }

  @Benchmark
  def fold_map(bh: Blackhole): Unit = {
    var i = 0
    var ret = 0L
    while (i < vOuter.length) {
      ret += vOuter(i) * vOuter(i)
      i += 1
    }
    bh.consume(ret)
  }

  @Benchmark
  def fold_map_filter(bh: Blackhole): Unit = {
    var i = 0
    var ret = 0L
    while (i < vOuter.length) {
      if (vOuter(i) % 2L == 0L)
        ret += vOuter(i) * vOuter(i)
      i += 1
    }
    bh.consume(ret)
  }

  @Benchmark
  def multiple_filters(bh: Blackhole): Unit = {
    var i=0
    var ret=0L
    while (i < vOuter.length) {
      if (vOuter(i) > 1 && vOuter(i) > 2 && vOuter(i) > 3 && vOuter(i) > 4 && vOuter(i) > 5 && vOuter(i) > 6 && vOuter(i) > 7)
        ret += vOuter(i)
      i += 1
    }
    bh.consume(ret)
  }

  @Benchmark
  def multiple_maps(bh: Blackhole): Unit = {
    var i=0
    var ret=0L
    while (i < vOuter.length) {
      ret += vOuter(i) * 1 * 2 * 3 * 4 * 5 * 6 * 7
      i += 1
    }
    bh.consume(ret)
  }

  @Benchmark
  def flatMap_map_sum(bh: Blackhole): Unit = {
    var d, dp = 0
    var ret = 0L
    while (d < vOuter.length) {
      dp = 0
      while (dp < vInner.length) {
        ret += vOuter(d) * vInner(dp)
        dp += 1
      }
      d += 1
    }
    bh.consume(ret)
  }

  @Benchmark
  def flatMap_take(bh: Blackhole): Unit = {
    var counter1 = 0
    var counter2 = 0
    var ret = 0L
    var n = 0
    var flag = true
    val size1 = vOuter.length
    val size2 = vInner.length
    while (counter1 < size1 && flag) {
      val item1 = vOuter(counter1)
      while (counter2 < size2 && flag) {
        val item2 = vInner(counter2)
        ret = ret + item1 * item2
        counter2 += 1
        n += 1
        if (n == shortRangingFactor)
          flag = false
      }
      counter2 = 0
      counter1 += 1
    }
    bh.consume(ret)
  }
}