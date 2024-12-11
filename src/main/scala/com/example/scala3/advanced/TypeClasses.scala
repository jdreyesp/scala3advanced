package com.example.scala3.advanced

import java.util.UUID

object TypeClasses extends App {

  // 1 - type class definition
  trait HTMLSerializer[A] {
    def serialize(value: A): String
  }

  case class User(id: UUID, name: String)

  // 2 - type class instantiation
  given userSerializer: HTMLSerializer[User] with {
    override def serialize(value: User): String =
      s"<div>User ${value.id} with name ${value.name}</div>"
  }

  /* Advantages:
    - WE can extend the HTMLSerializer trait (so we have extensability (specially useful in when developing libraries))
   */

  // 3- type class utilisation (let's say this is the client code outside of your library)
  def serialize[A](value: A)(using serializer: HTMLSerializer[A]): String =
    serializer.serialize(value)

  println(
    serialize(User(UUID.randomUUID(), "Bob"))
  ) // This will compile since we have a userSerializer instance above

  // 4 - We can simplify the usage (and enhance the expresiveness) of our type classes by using extension methods
  object HTMLSyntax {
    extension [A](value: A)
      def toHTML(using serializer: HTMLSerializer[A]): String =
        serializer.serialize(value)
  }

  import HTMLSyntax.*
  println(User(UUID.randomUUID(), "Alice").toHTML)
}
