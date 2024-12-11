package com.example.scala3.advanced.typesystem

object AdvancedInheritance extends App {

  trait Cold {
    def print() = println("Cold")
  }

  trait Green extends Cold {
    override def print() = {
      println("Green")
      super.print()
    }
  }

  trait Blue extends Cold {
    override def print() = {
      println("Blue")
      super.print()
    }
  }

  trait Red {
    def print() = println("Red")
  }

  class White extends Red with Green with Blue {
    override def print(): Unit = {
      println("White")
      super.print()
    }
  }

  new White().print()

  /* From type linearization:
        Cold = AnyRef with <Cold>
        Green = AnyRef with <Cold> with <Green>
        Blue = AnyRef with <Cold> with <Blue>
        Red = AnyRef with <Red>
        White = AnyRef with <Red> with <Green> with <Blue> with <White>
        -->
        White = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>
        -->
        References to super goes from right to left, this is:
            white will call blue.print.
            blue will call green.print.
            green will call cold.print
            cold WON'T call red.print since cold does not inherit red
   */
  /* Result of the println:
        - white
        - blue
        - green
        - cold
   */
}
