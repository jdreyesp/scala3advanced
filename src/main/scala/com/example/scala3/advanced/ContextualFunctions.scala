package com.example.scala3.advanced

object ContextualFunctions extends App {

  def functionWithoutContextualArguments(nonContextArgInt: Int)(
      nonContextArgString: String
  ): String = nonContextArgString
  def functionWithContextualArguments(nonContextArgInt: Int)(using
      contextArgString: String
  ): String = contextArgString

  // eta-expansion
  val noContextArgsFunction = functionWithoutContextualArguments
  // val contextArgsFunction = functionWithContextualArguments // Does not compile since it expects a given

  // Scala 3 syntax `?=>` (meaning that the argument is contextual). Caveate: We NEED to specify the function type
  val contextArgsFunction: Int => String ?=> String =
    functionWithContextualArguments

  // Usage
  val someResult = contextArgsFunction(3)(using "Scala")
  println(someResult)

  // Real world example: Futures
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext

  val incrementAsync: ExecutionContext ?=> Int => Future[Int] = x =>
    Future(
      x + 1
    ) // This works since the needed ExecutionContext is imported through the contextual function definition
}
