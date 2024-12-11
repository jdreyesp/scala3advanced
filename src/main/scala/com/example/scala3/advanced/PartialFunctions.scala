package com.example.scala3.advanced

object PartialFunctions extends App {

  val myPF: PartialFunction[Int, Int] = {
    case 35 => 46
    case 1  => 2
  }

  val myPF2: PartialFunction[Int, Int] = { case 45 =>
    10
  }

  println(myPF(35))
  println(myPF.orElse(myPF2)(45))

}
