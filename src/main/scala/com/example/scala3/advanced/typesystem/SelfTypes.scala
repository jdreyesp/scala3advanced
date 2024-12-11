package com.example.scala3.advanced.typesystem

import java.time.Year

object SelfTypes {

  trait Instrumentalist {
    def play(): Unit
  }

  // This is a self-type, meaning: whoever implements singer MUST also implements Instrumentalist
  // Note: 'self' can be any name. It's NOT a lambda
  trait Singer { self: Instrumentalist =>
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

  // This won't compile
  // class Vocalist extends Singer {}

  // Same goes for subclasses. If I define:
  trait Guitarist extends Instrumentalist {
    override def play(): Unit = println("some guitar solo")
  }

  // I can still say
  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // self-types vs inheritance
  // With inheritance, when B extends A we say that "B is an A"
  // With self-type, when we have trait B { self: A => ... } we say that "B requires A",
  // but they are not related to each other besides that

  // Usages of self-type

  // 1. Most common scenario: cake-pattern (i.e. injecting dependant classes/traits in order to build a layered / composed trait). E.g:

  // Instagram-kind of domain
  trait ComponentLayer1 {
    def actionLayer1(x: Int): String
  }

  trait ComponentLayer2 { self: ComponentLayer1 =>
    def actionLayer2(x: String): Int
  }

  trait Application { self: ComponentLayer1 & ComponentLayer2 =>
    // your main API
  }

  trait Picture extends ComponentLayer1
  trait Stats extends ComponentLayer1

  trait ProfilePage extends ComponentLayer2 with Picture
  trait AnalyticsPage extends ComponentLayer2 with Stats

  trait AnalyticsApp extends Application with AnalyticsPage

  // 2. Preserving the 'this' instance in inner classes
  // Let's say we define a class like:
  class SingerWithInnerClass { self =>

    class Voice {
      def sing() = this.toString
      // Here, 'this' refers to the Voice class, not SingerWithInnerClass.
      // If we wanted to refer to teh SingerWithInnerClass, we should use 'self'
    }
  }

  // 3. Cyclical dependencies

  // We can't have cyclical inheritance
  // class X extends Y
  // class Y extends X

  // But we can have cyclical dependencies with self-types, like:
  trait X { self: Y => }
  trait Y { self: X => }

  class Z extends X with Y { ??? } // This is totally fine
}
