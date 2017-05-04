package streams

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

class Push[T : Default : ClassTag](val sF: (T => Boolean) => Boolean) {

  def map[R : ClassTag](f: T => R) = {
    val new_sF = (iterf: R => Boolean) => sF(v => iterf(f(v)))

    new Push(new_sF)
  }

  def flatMap[R : ClassTag](f: T => Push[R]) = {
    val new_sF = (iterf: R => Boolean) => sF(v => {
      val inner : Push[R] = f(v)
      inner.sF(iterf)
    })

    new Push(new_sF)
  }

  def take(n : Int) = {
    var count : Int = 0
    val new_sF = (iterf: T => Boolean) => sF(v => {
      if (count < n)
        iterf(v)
      count += 1
      true
    })

    new Push(new_sF)
  }

  def toPull() : Pull[T] = {
    val buffer = ArrayBuffer[T]()

    sF(v => {
      buffer += v
      true
    })

    Pull(buffer.toArray[T])
  }

  def filter(pred: T => Boolean) = {
    val new_sF = (iterf: T => Boolean) => sF(v => {
      if (pred(v))
        iterf(v)
      else
        false
    })

    new Push(new_sF)
  }

  def foldLeft[A : ClassTag](a: A)(op: (A, T) => A): A = {
    var acc = a
    sF(v => {
      acc = op(acc, v)
      true
    })
    acc
  }
}

object Push {
  def apply[T : ClassTag](xs: Array[T]) = {
    val new_sF = (iterf: T => Boolean) => {
      var i = 0
      val size = xs.length
      while (i < size) {
        iterf(xs(i))
        i += 1
      }
      true
    }

    new Push(new_sF)
  }
}

