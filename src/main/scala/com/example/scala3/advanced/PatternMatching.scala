package com.example.scala3.advanced

object Even extends App {
  def unapply(arg: Int): Boolean = arg % 2 == 0

  val n: Int = 42
  val myPattern = n match {
    case Even() => println("Nice! You got an even number")
  }
}

object UnapplyObject extends App {

  class Person(val name: String, val age: Int)

  object Person {
    // This does not have to be an Option[(String, Int)], this needs to be a class that implements isEmpty() and get(),
    // what's really used by the compiler using reflection
    def unapply(person: Person): Option[(String, Int)] =
      if person.age < 21 then None
      else Some((person.name, person.age))
  }

  val matchPerson = Person("Nick", 35) match {
    case Person(name, age) => println(s"Hello $name")
  }

}

object UnapplySeq extends App {

  trait MyList[A] {
    def head: A = throw new NoSuchElementException()
    def tail: MyList[A] = throw new NoSuchElementException()
  }
  case class Empty[A]() extends MyList[A]

  case class Cons[A](override val head: A, override val tail: MyList[A])
      extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if list == Empty() then Some(Seq.empty)
      else
        unapplySeq(list.tail).map(restOfSequence => list.head +: restOfSequence)
  }

  val matchList: Cons[Int] = Cons(1, Cons(2, Cons(3, Empty())))

  matchList match {
    case MyList(1, _*) => println("It's a varargs list")
    case _             => println("Some other list")
  }
}
