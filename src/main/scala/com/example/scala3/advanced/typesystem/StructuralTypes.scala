package com.example.scala3.advanced.typesystem

// Special package!
import reflect.Selectable.reflectiveSelectable

object StructuralTypes extends App {

  // duck-typing (coming from duck testing, that says:
  // 'If something quacks like a duck, looks like a duck and walks like a duck... then it's probably a duck!'
  // )
  type SoundMaker = { // Structural type
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark!")
  }

  class Car {
    def makeSound(): Unit = println("vroom!")
  }

  // duck-typing normally comes from dynamic languages, like Python.
  // So here, in Scala, this is called `compile-time duck-typing`
  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  dog.makeSound() // This is done through reflection (slow)
  car.makeSound() // This is done through reflection (slow)

  // Why structural types are useful?? It allows to refine existing types. Let's say we have:
  abstract class Animal {
    def eat(): String
  }

  // I can refine it with
  type WalkingAnimal = Animal {
    def walk(): Int
  }

  // Real world scenario
  // Let's say I have Java closeable resources, and my own custom closeable resource
  type JavaCloseable = java.io.Closeable
  class CustomCloseable {
    def close(): Unit = println("I'm closing")
    def closeSilently(): Unit = println("I'm silently closing")
  }

  // If I want to define a method that closes a resource that we know that it's a JavaCloseable or
  // a CustomCloseable, but they are not implemented in such a way that they are connected to each other,
  // then I have to connect them in my code. The naive solution would be to think that I can define the method
  // with union type, like:
  // def closeResource(closeable: JavaCloseable | CustomCloseable): Unit =
  //  closeable.close()

  // but this does not work, the compiler complains that the close method is not a method of the union type.
  // Solution, use structural type:
  type UnifiedCloseable = {
    def close(): Unit
  }

  // Now we can say:
  def closeResource(closeable: UnifiedCloseable): Unit = closeable.close()

  val javaCloseable: JavaCloseable = new JavaCloseable {
    override def close(): Unit = println("Closing with Java!")
  }

  // And through reflection this will work
  closeResource(javaCloseable)

  // As an alternative, we can also use the shorter syntax notation for the structural type:
  def closeResource_v2(closeable: { def close(): Unit }): Unit =
    closeable.close()
}
