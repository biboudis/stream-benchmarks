package streams.Tree

import streams.{Pull, Push}

import scala.reflect.ClassTag

class Tree[+T]
case object Leaf extends Tree[Nothing]
case class Node[T](val left: Tree[T], val elem: T, val right: Tree[T]) extends Tree[T]


trait TreeOps {

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
        traverse_cps(left, f, (ignore : Unit) => {
          f(element)
          traverse_cps(right, f, k)
        })
      }
    }
  }

  def toPush[T : ClassTag, S](tree : Tree[T]) : Push[T]= {
    val new_sF = (iterf: T => Boolean) => {
      traverse_cps(tree, iterf, ((result : S) => result))
      true
    }
    new Push(new_sF)
  }

  class Kiter[T]
  case object Stop extends Kiter[Nothing]
  case class Left[T](val left : Kiter[T], val elem: T, val right : Tree[T]) extends Kiter[T]

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

  object TreeOps{}
}

