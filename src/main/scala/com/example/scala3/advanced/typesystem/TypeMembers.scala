package com.example.scala3.advanced.typesystem

object TypeMembers extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType
    type BoundedAnimal <: Animal
    type SuperboundedAnimal >: Dog <: Animal
    type AnimalAlias = Cat
    type NestedOption =
      List[Option[Option[Int]]] // often used to simplify nested types
  }

  // Using type members
  val oc = new AnimalCollection
  val myAnimal: oc.AnimalType = ???

  val aDog: oc.SuperboundedAnimal = new Dog
  val aCat: oc.AnimalAlias =
    new Cat // it works since AnimalAlias is just an alias for Cat

  // .type
  type CatType = aCat.type
}
