package com.example.scala3.advanced.typesystem

sealed trait Animal
final class Dog extends Animal
final class Cat extends Animal

object Covariance extends App {

  // Variance question for lists: if Dog extends Animal, does List[Dog] extends List[Animal]?

  // by default, based on Scala List implementation, the answer is YES.
  // because lists are COVARIANT:
  // class List[+A] // COVARIANT

  // If the type/class definition is with A (no sign), that means it is INVARIANT.
  // class MyList[A] // INVARIANT

  // In Java world, everything is INVARIANT (i.e. we can't say:
  // val myJavaList: java.util.ArrayList[Animal] = new java.util.ArrayList[Dog]  // Compilation error
  // )

  class MyList[+A]
  val myDogsList: MyList[Animal] =
    new MyList[Dog] // This compiles perfectly since Dog <: Animal
}

object Contravariant extends App {

  sealed trait Animal
  final class Dog extends Animal

  // CONTRAVARIANT - if a vet can heal dogs, for sure (it's implicit) that he/she can heal animals
  // So contravariance can be seen the other way around as variance
  sealed trait Vet[-A] {
    def heal(animal: A): Boolean
  }

  val myVet: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal): Boolean = {
      println("You're ok!")
      true
    }
  }
}

/*
Rule of thumb when applying variance:
    - If your class PRODUCES or RETRIEVES values (e.g. a list) then it should be COVARIANT
    - If your class ACTS ON or CONSUMES values (e.g. a vet) then it should be CONTRAVARIANT
    - Otherwise, use INVARIANT
 */

object VariancePosition extends App {

  /* 1. class Vet[-T](val favoriteAnimal: T) */
  // This won't compile since `val` and `var` fields are COVARIANT,
  // since you can have the following situation otherwise:

  // trait Animal
  // class Cat extends Animal

  // val garfield = new Cat
  // val myVet: Vet[Animal] = new Vet[Animal](garfield)
  // val myDogVet: Vet[Dog] = myVet //possible, since myVet is a Vet[Animal]
  // val aDog: Dog = aDogVet.favoriteAnimal // must be a Dog - type conflict!!

  /* 2. class MutableOption[+T](var contents: T) */

  // This won't compile since `var` fields are in CONTRAVARIANT position, explained with this example:

  // val maybeAnimal: MutableOption[Animal] = new MutableOption[Dog](new Dog) //this works since MutableOption[+T]
  // maybeAnimal.contents = new Cat  // Because contents is a var, I can set it with a different type - type conflict!!

  // So this leads us to say that `var` fields are in COVARIANT and in CONTRAVARIANT, (meaning that they are in INVARIANT position)

  /* 3. class MyList[+T] { def add(element: T): MyList[T] = ??? } */

  // This won't compile since types of method arguments are in CONTRAVARIANT position, because I could do:

  // val myAnimalList: MyList[Animal] = MyList[Cat]()
  // val myBiggerAnimalList = myAnimalList.add(Dog()) // type conflict!!!

  /* 4. Return method types */

  // Since return method types PRODUCE a new value, its position is COVARIANT, example:
  // abstract class Vet2[-T] { def heal(): T }  // this won't compile since T is forced to be defined here as CONTRAVARIANT, and it's not

  // val myVet: MyVet2[Dog] = new MyVet2[Animal]
  // val myCat: Animal = myVet.heal()   // type conflict!!!

  // Then how to fix variance positions?

  // COVARIANT example
  class LList[+A](val animalList: List[A]) {
    def add[B >: A](
        item: B
    ): LList[B] = new LList(animalList :+ item)
  }

  // Widening the type so that:
  val myAnimalList: LList[Animal] = new LList[Dog](List(new Dog()))
  val myBiggerAnimalList = myAnimalList.add(new Cat())
  println(
    myBiggerAnimalList.animalList
  ) // I can now mix cats and dogs because it's a list of animals :)

  // CONTRAVARIANT example
  sealed trait Vehicle
  class Car extends Vehicle
  class Supercar extends Car
  class RepairShop[-A <: Vehicle] {
    def repair[B <: A](vehicle: B): B = vehicle
  }

  // Shortening the type so that:
  val myVW = new Car()
  val myLamborghini = new Supercar()
  val myRepairShop: RepairShop[Car] = new RepairShop[Vehicle]()
  myRepairShop.repair(myVW) // I can repair VW because it's a car
  myRepairShop.repair(
    myLamborghini
  ) // I can also repair Lambos because it's a car
}
