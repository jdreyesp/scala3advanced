package com.example.scala3.advanced

import scala.collection.parallel.ParSeq
import scala.collection.parallel.CollectionConverters.*

object ParallelCollections extends App {

  val aList = (1 to 30000000).toList
  // val parList: ParSeq[Int] = aList.par
  // val aParallelizedIncrementedList = parList.map(_ + 1)

  def measure(expression: => Unit): Long = {
    val time = System.currentTimeMillis()
    expression
    System.currentTimeMillis() - time
  }

  println(measure(aList.map(_ + 1)))
  println(measure(aList.par.map(_ + 1)))
}

object DemoUndefinedOrder extends App {
  val aList = (1 to 1000).toList
  val reduction = aList.reduce(
    _ - _
  ) // Usually not a good idea since - is not associative (i.e. 1 - 2 - 3 is not the same as 1 - (2 - 3))
}

object RaceConditionExample extends App {
  var sum = 0
  (1 to 1000).toList.par.foreach(elem =>
    sum += elem
  ) // Here there's a huge contention over sum variable, since elems are being
  // processed in parallel, and all threads are using the same pointer to sum
  println(sum)
}
