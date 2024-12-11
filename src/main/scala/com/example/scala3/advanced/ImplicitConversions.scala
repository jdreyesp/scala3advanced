package com.example.scala3.advanced

import scala.language.implicitConversions

/*
  ImplicitConversion can be seen as custom autoboxing between two types
 */
object ImplicitConversions extends App {

  // Implicit conversions will give us the ability to have implicit conversions between types (we need to
  // specify the conversions)
  case class Person(name: String)

  given stringToPerson: Conversion[String, Person] with {
    override def apply(x: String): Person = Person(x)
  }

  println("Daniel".name)

  def processPerson(person: Person): String =
    if (person.name.startsWith("J")) "OK"
    else "NOT OK"

  // Thanks to the implicit conversion, we can use this. The compiler will rewrite this into processPerson(Person("Jane"))
  val janePerson = processPerson("Jane")
}

/** Note: In scala 2, implicit conversions were declared as:
  *
  * implicit def string2Person(x: String): Person = Person(x)
  *
  * This was dangerous on synthetise (define a implicit def from another
  * implicit def), since implicit keyword can mean many different things (that's
  * why it will be phased out at some point)
  */
