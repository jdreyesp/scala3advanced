package com.example.scala3.advanced.typesystem

object FBoundedPolymorphism {

  // If I have this domain
  trait Animal {
    def breed: List[Animal]
  }

  class Cat extends Animal {
    // But here I could also have said = List (new Cat, new Dog)! I want to avoid this
    override def breed: List[Animal] = List(new Cat, new Cat)
  }

  class Dog extends Animal {
    override def breed: List[Animal] = List(new Dog, new Dog)
  }

  // The naive solution would be forcing List[Animal] as List[Cat] for class Cat and List[Dog] for class Dog,
  // but there's a better (and more generic) way:

  object FBP {
    trait Animal[A <: Animal[A]] { // Recursive type, or fancier name: F-Bounded Polymorphism
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
      override def breed: List[Animal[Cat]] = List(new Cat)
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = List(new Dog, new Dog)
    }

    // I still can mess up FBP
    class Crocodile extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ???
    }
    // how to solve this mess? solution below
  }

  // solution about the mess above: mix FBP with self-types:
  object FBPWithSelfTypes {
    trait Animal[A <: Animal[A]] { self: A =>
      def breed: List[Animal[A]]
    }

    // Therefore I can't do
    // class Crocodile extends Animal[Dog] -> does not compile

  }

  // Where could FBP be used? Some ORM (object relational mapping) libraries
  trait Entity[E <: Entity[E]]
  // Example: Java sorting library
  class Person extends Comparable[Person] { // FBP
    override def compareTo(o: Person): Int = ???
  }

}
