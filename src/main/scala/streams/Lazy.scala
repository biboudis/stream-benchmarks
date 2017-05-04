//package streams
//
//import scala.collection.generic.{CanBuildFrom, HasNewBuilder}
//
//trait X
//trait N extends X
//trait S extends X
//trait L extends X
//
//trait Lazy[A, Repr, T <: X] extends HasNewBuilder[A, Repr] {
//
//  protected[this] type Self = Repr
//
//  def repr: Repr = this.asInstanceOf[Repr]
//
//  def map[B, That](f: A => B): That
//
//  def zip[B, That](that: Lazy[B, Repr]): That
//
//  def foldLeft[B](z: B)(op: (A, B) => B): B
//}
//
//object Lazy {
//  def apply[T, Repr](xs: Array[T]): Lazy[T, Repr] = ???
//}
//
//trait LazyPush[A, T <: X] extends Lazy[A, Push[A], T] {
//  def map[B, Push[A]](f: A => B): this.type = ???
//
//  def map2[B, Push[A]](f: A => B)(implicit ev: T =:= L): this.type = ???
//
//  def foldLeft[B](z: B)(op: (A, B) => B): B
//}
//
//trait LazyPull[A] extends Lazy[A, Pull[A]] {
//  def map[B, Pull[A]](f: A => B): Pull[A] = ???
//
//  def foldLeft[B](z: B)(op: (A, B) => B): B
//}
//
//object Test {
//  implicit class pullOnly[A, Repr](stream: Lazy[A, Repr, L]) {
//    def zip[B, Pull[A]](that: LazyPull[B]): this.type = ???
//
//  }
//  implicit class neutralZip[A, Repr](stream: Lazy[A, Repr, N]) {
//    def zip[B, Pull[A]](that: LazyPull[B]): Lazy[A, Repr, L] = ???
//  }
//
//  val v : Array[Int] = Array(1,2,3)
//
//  Lazy(v).map(x => x * 1).map(x => x * 2) // to be push
//
//  Lazy(v).map(x => x * 1)
//    .zip(Lazy(v).map(x => x * 2))
//    .foldLeft(List[(Int, Int)]())((b, a) => a :: b) // to be pull
//
//  type T = Map :: FlatMap[Map :: Filter :: HNil] :: Zip :: FoldLeft :: HNil
//
//  trait Lazy[T <: HList] {
//    def pullStuff(implicit ev: ContainsZip[T]) = ...
//  }
//
//
//  implicitly[ContainsZip[Map :: FlatMap[Map :: Filter :: HNil] :: Zip :: FoldLeft :: HNil]]
//  implicitly[ContainsZip[FlatMap[Map :: Filter :: HNil] :: Zip :: FoldLeft :: HNil]]
//
//  trait Nat
//  trait Zero extends Nat
//  trait Succ[N <: Nat] extends Nat
//
//  trait ContainsZip[T <: HList] {
//    type Out <: Nat
//  }
//
//  trait LowPrioContrainsZip {
//    implicit caseTail    [Head, Tail <: HList]
//      (implicit ev: ContainsZip[Tail])
//        : ContainsZip[Head :: Tail] { type Out = ev.Out } = null
//  }
//  object ContainsZip {
//    implicit caseZipFirst[Tail <: HList]
//         : ContainsZip[Zip :: Tail] { type Out = Zero } = null
//
//    implicit caseZipFirst[FlatMapArg <: HList, Tail <: HList, Prev <: Nat]
//      (implicit ev: ContainsZip[FlatMapArg] { type Out = Prev })
//         : ContainsZip[FlatMap[FlatMapArg] :: Tail] { type Out = Succ[Prev] } = null
//  }
//
//  def ContainsZip[T] = T match {
//    case Zip :: xs => true
//    case FlatMap(z) :: xs if ContainsZip[z] => true
//    case _ :: xs if ContainsZip[xs] => true
//  }
//
//
//
//}
