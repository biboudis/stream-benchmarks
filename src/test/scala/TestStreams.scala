/**
  * Created by bibou on 06/04/2017.
  */

import org.junit.Test
import streams._

class TestStreams {

  @Test
  def test_pull_sum_of_squares() = {
    val res = Pull(Array(1,2,3))
      .map(x => x * x)
      .foldLeft(0)(_+_)

    assert(res == 14)
  }

  @Test
  def test_pull_cart() = {
    val res = Pull(Array(1,2,3))
      .flatMap(x => Pull(Array(1,2,3)).map(y => x * y))
      .foldLeft(0)(_+_)

    assert(res == 36)
  }

  @Test
  def test_push_cart() = {
    val res = Push(Array(1,2,3))
      .flatMap(x => Push(Array(1,2,3)).map(y => x * y))
      .foldLeft(0)(_+_)

    assert(res == 36)
  }

  @Test
  def test_push_take() = {
    val res = Push(Array(1,2,3,4))
      .take(2)
      .foldLeft(0)(_+_)

    assert(res == 3)
  }

  @Test
  def test_pull_take() = {
    val res = Pull(Array(1, 2, 3, 4))
      .take(2)
      .foldLeft(0)(_ + _)

    assert(res == 3)
  }

  @Test
  def test_push_sum_of_squares() = {
    val res = Push(Array(1,2,3))
      .map(x => x * x)
      .foldLeft(0)(_+_)

    assert(res == 14)
  }

  @Test
  def test_view_sum_of_squares() = {
    val src : strawman.collection.View[Int] = strawman.collection.ArrayView(Array(1,2,3))

    val res = src.map(x => x * x).foldLeft(0)(_+_)

    assert(res == 14)
  }

  @Test
  def test_pull_to_push_sum_of_squares() = {

    val res = Pull[Int](Array(1,2,3))
      .map(x => x * x)
      .toPush()
      .foldLeft(0)(_+_)

    assert(res == 14)
  }

  @Test
  def test_push_to_pull_sum_of_squares() = {

    val res = Push[Int](Array(1,2,3))
      .map(x => x * x)
      .toPull()
      .foldLeft(0)(_+_)

    assert(res == 14)
  }
}
