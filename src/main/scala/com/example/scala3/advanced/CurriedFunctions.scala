package com.example.scala3.advanced

object CurriedFunctions extends App {

  def curriedAdd(x: Int)(y: Int): Int =
    x + y

  // converting methods to function values => eta-expansion => This returns a function with the rest of the curried arguments
  val addTo5 = curriedAdd(5)
  val nine = addTo5(4)
  println(nine)
}

object UnderscoresOnFunctions extends App {

  def concatenator(s1: String, s2: String, s3: String): String = s1 + s2 + s3

  val greetingByName = concatenator("Hello ", _: String, ", I'm a human!")
  val greetingByIntroOutro = concatenator(_: String, "Michael", _: String)

  println(greetingByName("Lucas"))
  println(greetingByIntroOutro("Hi! ", ", I'm another human!"))
}

// Always try to define defs with parenthesis
    // def myLambda() = 42
    // instead of
    // def myLambda = 42
// because if I have a by name method:
    // def byName(n: => Int) = n + 1
        // I can do: byName(myLambda())
        // But I can't do: byName(myLambda)
        // Since the compiler can't do eta-expansion of the latter.
