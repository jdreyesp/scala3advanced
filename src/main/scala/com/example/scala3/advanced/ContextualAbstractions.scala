package com.example.scala3.advanced

object Givens extends App {

  val aList = List(2, 1, 4, 3)
  val orderedList = aList.sorted

  // This affects previous aList.sorted
  given reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  val reverseOrderedList = aList.sorted(reverseOrdering)

  println(orderedList)
  println(reverseOrderedList)
}

object Givens_v2 extends App {
  case class Person(name: String, age: Int)

  given personOrder: Ordering[Person] = (p1: Person, p2: Person) =>
    p1.name.compareTo(p2.name)

  object PersonOrders {
    // Another possible syntax for given
    given personOrder2: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int = x.age.compareTo(y.age)
    }
  }

  val list = List(
    Person("Lindy", 15),
    Person("Bob", 22),
    Person("Alice", 34)
  ).sorted // This will take personOrder, not personOrder2 because of the scope this is placed in the code

  println(list)
}

object Using extends App {
  trait Combinator[A]:
    def combine(x: A, y: A): A

  given intCombinator: Combinator[Int] = (x, y) => x + y

  // Using word for expecting a given (implicit)
  def combineAll[A](l: List[A])(using combinator: Combinator[A]): A =
    l.reduce(combinator.combine)

  val result = combineAll(List(1, 2, 3, 4))
  println(result)

  // Context bound
  // Here [A: Combinator] will implicitly inject a combinator from the scope / context
  def combineInGroupsOf3[A: Combinator](l: List[A]): List[A] = {
    l.grouped(3).map(combineAll).toList
  }

  // synthesize: Create a new given instance based on another one
  given listOrdering(using Ordering[Int]): Ordering[List[Int]] with {
    override def compare(x: List[Int], y: List[Int]): Int = ???
  }

  // or even with generics!
  given listOrderingBasedOnCombinator[A](using
      ordering: Ordering[A]
  )(using combinator: Ordering[List[A]]): Ordering[List[A]] with {
    override def compare(x: List[A], y: List[A]): Int = ???
  }

  // Using regular values instead of a given
  val myCombinator: Combinator[Int] = new Combinator[Int] {
    override def combine(x: Int, y: Int): Int = x + y
  }

  // Here, we pass a value using the keyword 'using', that has a different meaning than the previous examples
  combineAll(List(1, 2, 3, 4))(using myCombinator)
}

/*
    Places / contexts where the compiler looks for givens:
        1. Local context
        2. Import contexts (import PersonGivens...)
        3. Companion objects of all types involved in the method signature

    Note: same for extension methods!
 */

object OrganisingContextualAbstractions extends App {

  case class Person(name: String, age: Int)

  val persons = List(
    Person("Anna", 25),
    Person("Paul", 53)
  )

  object PersonGivens {
    given ageOrdering: Ordering[Person] with
      override def compare(x: Person, y: Person): Int = x.age.compareTo(y.age)
  }

  // Special import for givens
  import PersonGivens.{given Ordering[Person]}
  // or
  import PersonGivens.given

  // Warning:
  import PersonGivens.* // This DOES NOT import givens! (but it imports extension methods!) (it also imports implicits from Scala2)
  persons.sorted
}

/*
    Good practice tips:
        - When there's a default given, add it to the companion object of the class
        - When there's many possibilities but ONE DOMINANT one, add that one in the companion object and the rest in other object
        - When there's many possibilities with equal importance, add each of the givens in different objects
 */
