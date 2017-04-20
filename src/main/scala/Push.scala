/**
  * Created by bibou on 06/04/2017.
  */

package streams

class Push[T](val sF: (T => Unit) => Unit) {

  def map[R](f: T => R) = {
    val new_sF = (iterf: R => Unit) => sF(v => iterf(f(v)))
    new Push(new_sF)
  }

  def fold[A](a: A)(op: (A, T) => A): A = {
    var acc = a
    sF(v => {
      acc = op(acc, v)
    })
    acc
  }

  def flatMap[R](f: T => Push[R]) = {
    val new_sF = (iterf: R => Unit) => sF(v => {
      val inner : Push[R] = f(v)
      inner.sF(iterf)
    })

    new Push(new_sF)
  }

  def take(n : Int) = {
    var count : Int = 0
    val new_sF = (iterf: T => Unit) => sF(v => {
      if (count < n)
        iterf(v)

      count += 1
    })
    new Push(new_sF)
  }


  def filter(pred: T => Boolean) = {
    val new_sF = (iterf: T => Unit) => sF(v => {
      if (pred(v)) iterf(v)
    })

    new Push(new_sF)
  }
}

object Push {
  def of[T](xs: Array[T]) = {
    val new_sF = (iterf: T => Unit) => {
      var i = 0
      val size = xs.length
      while (i < size) {
        iterf(xs(i))
        i += 1
      }
    }
    new Push(new_sF)
  }
}

