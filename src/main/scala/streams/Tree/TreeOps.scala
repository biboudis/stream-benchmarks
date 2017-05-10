package streams.Tree

import streams.{Pull, Push}

import scala.reflect.ClassTag

sealed trait Tree[+T]
case object Leaf extends Tree[Nothing]
case class Node[T](left: Tree[T], elem: T, right: Tree[T]) extends Tree[T]


class TreeOps {

  // from http://gallium.inria.fr/blog/generators-iterators-control-and-continuations/
  def deep(depth: Int): Tree[Int] = {
    depth match {
      case 0 => Leaf
      case n: Int => {
        val t: Tree[Int] = deep(n - 1)
        Node[Int](t, n, t)
      }
    }
  }

  def traverse[T](tree : Tree[T], f : (T => Boolean)) : Unit = {
    tree match {
      case Leaf => ()
      case Node(left, element, right) => {
        traverse(left, f)
        f(element)
        traverse(right, f)
      }
    }
  }

  def traverse_cps[T, S](tree : Tree[T], f : (T => Boolean), k: (S => S)) : Unit = {
    tree match {
      case Leaf => k(_)
      case Node(left, element, right) => {
        traverse_cps(left, f, (_: Unit) => {
          f(element)
          traverse_cps(right, f, k)
        })
      }
    }
  }

  def toPush[T : ClassTag, S](tree : Tree[T]) : Push[T]= {
    val new_sF = (iterf: T => Boolean) => {
      traverse_cps(tree, iterf, (result: S) => result)
      true
    }
    new Push(new_sF)
  }

  class Kiter[T]
  case object Stop extends Kiter[Nothing]
  case class Left[T](left : Kiter[T], elem: T, right : Tree[T]) extends Kiter[T]

  def traverse_cps_defun[T](tree : Tree[T], f : (T => Boolean), k : Kiter[T]) : Unit = {
    tree match {
      case Leaf => run(f, k)
      case Node(left, element, right) => {
        traverse_cps_defun(left, f, Left (k, element, right))
      }
    }
  }

  def run[T](f: (T) => Boolean, k: Kiter[T]): Unit = {
    k match {
      case Stop => ()
      case Left (k, x, right) => {
        f(x)
        traverse_cps_defun(right, f, k)
      }
    }
  }

  def traverse_cps_defun2[T](tree : Tree[T], f : (T => Boolean), k : Tree[T]) : Unit = {
    tree match {
      case Leaf => run2(f, k)
      case Node(left, element, right) => {
        traverse_cps_defun2(left, f, Node (k, element, right))
      }
    }
  }

  def run2[T](f: (T) => Boolean, k: Tree[T]): Unit = {
    k match {
      case Leaf => ()
      case Node (k, x, right) => {
        f(x)
        traverse_cps_defun2(right, f, k)
      }
    }
  }

  def toPush2[T : ClassTag, S](tree : Tree[T]) : Push[T]= {
    val new_sF = (iterf: T => Boolean) => {
      traverse_cps_defun2(tree, iterf, Leaf)
      true
    }
    new Push(new_sF)
  }

  def fill[T](f: Pull[T], n: Int): (Boolean, Tree[T]) = {
    n match {
      case 0 => (false, Leaf)
      case _ => {
        val (finished, left) = fill(f, n - 1)
        if(finished)
          (finished, left)
        else
          fill_right(f, n, left)
      }
    }
  }

  def fill_right[T](f: Pull[T], n: Int, left: Tree[T]): (Boolean, Tree[T]) = {
    if(!f.sIter.hasNext)
      (true, left)
    else {
      val (finished, right) = fill(f, n - 1)

      (finished, Node(left, f.sIter.next(), right))
    }
  }

  def ofIter[T](f: Pull[T]) : Tree[T] = {
    def loop(f: Pull[T], n: Int, left: Tree[T]): Tree[T] = {
      fill_right(f, n, left) match {
        case (true, t) => t
        case (false, left) => loop(f, n + 1, left)
      }
    }

    loop(f, 1, Leaf)
  }
}