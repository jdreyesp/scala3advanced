package com.example.scala3.advanced

trait Transformer[A, B]:
  def apply(a: A): B

trait Predicate[A]:
  def check(a: A): Boolean

abstract class LList[A] {
  def head: A
  def tail: LList[A]
  def isEmpty: Boolean
  def add(element: A): LList[A]
  def map[B](t: Transformer[A, B]): LList[B]
}

object LListWithPrimitives extends App {

  class Empty[A] extends LList[A] {

    override def head: A = throw new NoSuchElementException

    override def tail: LList[A] = throw new NoSuchElementException

    override def isEmpty: Boolean = true

    override def add(element: A): LList[A] = new Cons(element, Empty())

    override def toString(): String = "[]"

    override def map[B](t: Transformer[A, B]): LList[B] = Empty()

  }

  class Cons[A](override val head: A, override val tail: LList[A])
      extends LList[A] {

    override def isEmpty: Boolean = false
    override def add(element: A): LList[A] = Cons(head, tail.add(element))

    override def toString(): String = s"[$head, $tail]"

    override def map[B](t: Transformer[A, B]): LList[B] =
      new Cons(t(head), tail.map(t))
  }

  println(Cons(1, Cons(2, Empty())))
  val doubleTransformer = new Transformer[Int, Long] {
    override def apply(a: Int): Long = a * 2
  }

  println(Cons(1, Cons(2, Empty())).map(doubleTransformer))
}

object Ffunctions extends App {

  /**   1. A function takes 2 strings and concatenate them 2. Replace predicate
    *      / transformer with the function types if necessary 3. Define a
    *      function that takes an Int as argument and returns ANOTHER FUNCITON
    *      as a result
    */

  // 1
  val myFunction = (a: String, b: String) => a + b

  val myFunction2 = (a: Int) => (a: Int) => Int

  val myFunction3 = { (s: String) =>
    s.toString()
  }

  val myFunction4 = Seq("Hello").flatMap((a: String) => a + a).mkString

  println(myFunction4)
  // println(myFunction3("hello!"))

  def printIterable() = {
    val iterable: Iterable[Int] = List(1, 2, 3)
    val it = iterable.iterator
    while (it.hasNext) {
      println(it.next())
    }
  }

}

object MapFlatMap extends App {
  def map[A, B](a: A, f: A => B): B = f(a)
  type C[+B] = List[B]
  def flatMap[A, B](a: A, f: A => C[B]): C[B] = f(a)
  // val f = (a: Int) => List(a * 2, a * 3)
  // [1, 2, 3].flatMap(f) => [2, 3], [4, 6], [6, 9]
}

object BracelessSyntax extends App {
  if 2 > 3 then
    val result = "bigger"
    println(result)
  else
    val result = "smaller"
    println(result)

  for i <- (1 to 10)
  yield println(i)
}
