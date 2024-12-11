package com.example.scala3.advanced

object LazyFunctions extends App {

  def byNameMethod(x: => Int): Int =
    x + x + x + 1

  def byNeedMethod(x: => Int): Int =
    lazy val lazyX = x
    // It's evaluated only once
    lazyX + lazyX + lazyX + 1

  def retrievingMagicValue() = {
    println("Waiting")
    Thread.sleep(1000)
    42
  }

  // println(byNameMethod(retrievingMagicValue()))
  println(byNeedMethod(retrievingMagicValue()))
  // def byNeedFn()
}

object LazyFunctionsExercise extends App {

  abstract class LzList[A] {
    def isEmpty: Boolean
    def head: A
    def tail: LzList[A]

    // utilities
    // because this method ends in : it could be written right-associative
    def #::(element: A): LzList[A] // prepending
    def ++(
        another: => LzList[A]
    ): LzList[A] // This has to be by name since left and right side of the operator need to prevent breaking the lazy evaluation for this case (infinite list)

    // classics
    def foreach(f: A => Unit): Unit
    def map[B](f: A => B): LzList[B]
    def flatMap[B](f: A => LzList[B]): LzList[B]
    def filter(predicate: A => Boolean): LzList[A]
    def withFilter(predicate: A => Boolean): LzList[A] = filter(predicate)

    def take(
        n: Int
    ): LzList[A] // takes the first n elements from this lazy list
    def takeAsList(n: Int): List[A]
    def toList: List[A] // use this carefully on large lists
  }

  case class LzEmpty[A]() extends LzList[A] {

    override def isEmpty: Boolean = true

    override def head: A = throw new NoSuchElementException()

    override def tail: LzList[A] = this

    override def #::(element: A): LzList[A] = LzCons(element, this)

    override def ++(another: => LzList[A]): LzList[A] = another

    override def foreach(f: A => Unit): Unit = ()

    override def map[B](f: A => B): LzList[B] = LzEmpty[B]()

    override def flatMap[B](f: A => LzList[B]): LzList[B] = LzEmpty[B]()

    override def filter(predicate: A => Boolean): LzList[A] = this

    override def take(n: Int): LzList[A] =
      if n == 0 then this
      else throw new NoSuchElementException()

    override def takeAsList(n: Int): List[A] =
      throw new NoSuchElementException()

    override def toList: List[A] = Nil

  }

  class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A] {

    override def isEmpty: Boolean = false

    override lazy val head: A = hd

    override lazy val tail: LzList[A] = tl

    override def #::(element: A): LzList[A] = {
      LzCons(element, this)
    }

    override def ++(another: => LzList[A]): LzList[A] =
      LzCons(head, tail ++ another)

    override def foreach(f: A => Unit): Unit = {
      f(head)
      tail.foreach(f)
    }

    override def map[B](f: A => B): LzList[B] = {
      LzCons(f(head), tail.map(f))
    }

    override def flatMap[B](f: A => LzList[B]): LzList[B] = {
      f(head) ++ tail.flatMap(f)
    }

    override def filter(predicate: A => Boolean): LzList[A] = {
      if predicate(head) then
        LzCons(head, tail.filter(predicate)) // preserve lazy eval
      else tail.filter(predicate)
    }

    override def take(n: Int): LzList[A] = {
      def takeRecursive(n: Int, acc: => LzList[A]): LzList[A] =
        if n <= 0 then acc // Return accumulated list when n is 0 or less
        else
          LzCons(head, tail.take(n - 1)) // Prepend head and continue with tail

      takeRecursive(n, LzEmpty())
    }

    override def takeAsList(n: Int): List[A] = take(n).toList

    override def toList: List[A] = {

      def toListRecursive(lzList: LzList[A], acc: List[A]): List[A] = {
        lzList match {
          case LzEmpty() => acc
          case LzCons(headElem, tailElems) =>
            (acc :+ headElem) ++ toListRecursive(tailElems, acc)
        }
      }

      toListRecursive(this, List())
    }

  }

  object LzCons {
    def unapply[A](lzList: LzList[A]): Option[(A, LzList[A])] =
      Some(lzList.head, lzList.tail)
  }

  object LzList {
    def empty[A]: LzList[A] = LzEmpty()
    def apply[A](elems: A*): LzList[A] = from(elems.toList)
    def generate[A](start: A)(generator: A => A): LzList[A] =
      new LzCons(start, LzList.generate(generator(start))(generator))
    def from[A](list: List[A]): LzList[A] =
      list.reverse.foldLeft(LzList.empty) { (currentLzList, newElement) =>
        new LzCons(newElement, currentLzList)
      }
  }

  // Generates INFINITE list of natural numbers
  val naturals = LzList.generate(1)(n => n + 1)
  println(naturals.head)
  println(naturals.tail.head)

  val first100 = naturals.take(100)
  first100.foreach(println)

  // classics
  // println(naturals.map(n => n + 1).takeAsList(100))
  // println(naturals.flatMap(n => LzList(n, n + 1)).takeAsList(100))

  println(naturals.filter(n => n <= 10).takeAsList(9))

  // For
  val combinationsLazy: LzList[String] = for {
    number <- LzList(1, 2, 3)
    string <- LzList("black", "white")
  } yield s"$number-$string"

  println(combinationsLazy.toList)
}
