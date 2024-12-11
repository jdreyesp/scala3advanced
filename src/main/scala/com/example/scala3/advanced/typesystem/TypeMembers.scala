package com.example.scala3.advanced.typesystem

object TypeMembers extends App {

  type myType = Int

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

object PathDependantTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def process(inner: Inner) = println("hello")
  }

  // If I say
  val myOuterA = new Outer
  val myOuterB = new Outer
  val myInnerA = new myOuterA.Inner

  // I can't say this because different instances of the same inner class have different types (because they are path dependant)
  // val myInnerB: myOuterA.Inner = new myOuterB.Inner

  // The same way, this fails compilation:
  // myOuterB.process(myInnerA)
  // but this will work
  myOuterB.process(new myOuterB.Inner)

  // I can define this now
  extension (outer: Outer)
    def processGeneral(inner: Outer#Inner) = println("Hello general")

  // So I can now say:
  myOuterB.processGeneral(myInnerA)

  /* Why this could be useful?
  - This can be seen in some libraries, like Akka streams (e.g. Flow[Int, Int, NotUsed]#Repr)
  - It's also used in type-level programming
   */

  // Another example
  trait Record {
    type Key
    def defaultValue: Key
  }

  def getDefaultIdentifier(record: Record): record.Key = record.defaultValue

  // Then we can use the path-dependant type like this
  val getIdentifierFunc: Record => Record#Key = getDefaultIdentifier

}
