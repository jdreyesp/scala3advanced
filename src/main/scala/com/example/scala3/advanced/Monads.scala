package com.example.scala3.advanced

object Monads extends App {

  def listStory(): Unit = {
    val aList = List(1, 2, 3)

    // Let's say I have these functions and the pure one (no computation, simply it constructs a list from the int)
    val f = (x: Int) => List(x, x + 1)
    val g = (x: Int) => List(x, 2 * x)
    val pure = (x: Int) => List(x)

    // flatMap properties:

    // prop1: left identity
    val leftIdentity = pure(42).flatMap(f) == f(42)

    // prop2: right identity
    val rightIdentity = aList.flatMap(pure) == aList

    // prop3: associativity (functions can be invoked one by one: f `and then` g)
    /*
    [1,2,3].flatMap(x => [x, x+1]) = [1,2,2,3,3,4]
    [1,2,2,3,3,4].flatMap(x => [x, 2*x]) = [1,2,2,4,2,4,3,6,3,6,4,8]
    This can be expressed as:
    [1,2,2,4] = f(1).flatMap(g)
    [2,4,3,6] = f(2).flatMap(g)
    [3,6,4,8] = f(3).flatMap(g)
    the same as:
    [1,2,2,4,2,4,3,6,3,6,4,8] = f(1).flatMap(g) ++ f(2).flatMap(g) ++ f(3).flatMap(g)
    which can be expressed in general terms as:
    [1,2,3].flatMap(x => f(x).flatMap(g))

    So we're saying that flatmapping the whole thing is the same as flatmapping each of them in separate operations:
     */
    val associativity =
      aList.flatMap(f).flatMap(g) == aList.flatMap(x => f(x).flatMap(g))
  }

  def optionStory(): Unit = {

    val anOption = Option(42)

    val f = (x: Int) => Option(x + 1)
    val g = (x: Int) => Option(2 * x)
    val pure = (x: Int) => Option(x)

    // prop1: left-identity
    val leftIdentity = pure(42).flatMap(f) == f(42)

    // prop2: right-identity
    val rightIdentity = anOption.flatMap(pure) == anOption

    // prop3: associativity
    /*
        anOption.flatMap(f).flatMap(g) = Option(42).flatMap(x => Option(x + 1)).flatMap(y => Option(2 * x))
        = Option(43).flatMap(x => Option(2 * x))
        = Option(86)

        anOption.flatMap(x => f(x).flatMap(g)) = Option(42).flatMap(x = > Option(x + 1)).flatMap(y => Option(2 * y))
        = Option(42).flatMap(x => 2 * x + 2)
        = Option(86)
     */
    val associativity =
      anOption.flatMap(f).flatMap(g) == anOption.flatMap(x => f(x).flatMap(g))

    // MONADS = The ability to chain computations.

    // So data structures like List or Option are monads since their flatMaps apply to those 3 properties, that
    // allows us to chain computations
  }
}
