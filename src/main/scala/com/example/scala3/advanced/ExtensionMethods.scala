package com.example.scala3.advanced

object ExtensionMethods extends App {

  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name, nice to meet you!"
  }
  extension (s: String) def greetAsPerson: String = Person(s).greet

  println("Daniel".greetAsPerson)

  // It also works with generics
  extension [A](list: List[A]) def ends: (A, A) = (list.head, list.last)

  println(List(1, 2, 3, 4).ends)
}

object ExtendUsingCombo extends App {
  trait Combinator[A]:
    def combine(x: A, y: A): A

  // We can combine extends and using
  extension [A](list: List[A])
    def combineAll(using combinator: Combinator[A]): A =
      list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int): Int = x + y
  }

  // This finds the intCombinator
  List(1, 2, 3, 4).combineAll // or combineAll(List(1,2,3,4))
  // List("Hello").combineAll // This does not compile since there's no Combinator[String]
}

/* Note:
  In scala 2, extension methods were implicit classes (e.g.

  implicit class MyRichInteger(number: Int) {
    def isEven = number % 2 == 0
  }

  Note that the disadvantage of this was that the name was not really necessary (MyRichInteger), reason
  why this was removed in the Scala 3 extension methods
  )
 */
