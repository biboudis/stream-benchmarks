/**
  * Created by bibou on 06/04/2017.
  */

package streams

import java.util.NoSuchElementException

class Pull[T](val sIter: Iterator[T]) {

  def map[R](f: T => R) = {
    val new_sIter = new Iterator[R] {
      def hasNext = sIter.hasNext

      def next() = f(sIter.next)
    }
    new Pull(new_sIter)
  }

  def filter(pred: T => Boolean) = {
    val new_sIter : Iterator[T] = new Iterator[T] {

      var next_item : T = _

      def hasNext () : Boolean = {

        while (sIter.hasNext){
          next_item = sIter.next()

          if(pred(next_item))
            return true
        }

        return false
      }

      def next() : T = {
        if (!this.hasNext()) {
          throw new NoSuchElementException
        }

        return next_item
      }
    }

    new Pull(new_sIter)
  }

  def flatMap[R](f: T => Pull[R]) = {
    val new_sIter : Iterator[R] = new Iterator[R] {

      var current : Pull[R] = _
      var next_item : R = _

      def hasNext() : Boolean = {

        while (true) {

          while (current != null && current.sIter.hasNext) {
            return true
          }

          if (!sIter.hasNext)
            return false

          current = f(sIter.next())
        }

        return true
      }

      def next() : R = {
        if (!this.hasNext()) {
          throw new NoSuchElementException
        }

        return current.sIter.next()
      }
    }

    new Pull(new_sIter)
  }

  def take (n : Int) = {
    val new_sIter : Iterator[T] = new Iterator[T] {

      var count = 0

      def hasNext() : Boolean = {
        count < n  && sIter.hasNext
      }

      def next() : T = {

        if (!this.hasNext()) {
          throw new NoSuchElementException
        }

        count += 1

        return sIter.next
      }
    }

    new Pull(new_sIter)
  }


  def fold[A](a: A)(op: (A, T) => A): A = {
    var acc = a
    while (sIter.hasNext) {
      acc = op(acc, sIter.next())
    }
    acc
  }
}

object Pull {

  def of[T](xs: Array[T]) = {
    val new_sIter = new Iterator[T] {
      override val size = xs.length
      var i = 0

      def hasNext = {
        i < size
      }

      def next() = {
        if (i >= size)
          throw new Exception()
        val el = xs(i)
        i += 1
        el
      }
    }
    new Pull(new_sIter)
  }

}

