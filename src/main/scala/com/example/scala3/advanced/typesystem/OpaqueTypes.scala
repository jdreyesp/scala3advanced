package com.example.scala3.advanced.typesystem

object OpaqueTypes extends App {

  // Opaque types will be declared and used with all its power (if I have type CustomType = String I could
  // use the whole String API) inside the class they were declared. However, they can't get all its power
  // outside the class definition they were declared. It makes sense in cases you don't want to expose
  // how the type is related to another type, but only the usage of that type. Example:
  object Graphics {
    opaque type Color = Int
    opaque type ColorFilter <: Color = Int
    // note that opaque is different than private type. Private types are not exposed outside the class they were defined

    val Red: Color = 0xff000000
    val Green: Color = 0x00ff0000
    val Blue: Color = 0x0000ff00

    val halfTransparency: ColorFilter = 0x80 // 50% transparent
  }

  // Here, I import the opaque types, but I can't use them as Ints
  import Graphics.*
  // I cant say val myColor: Color = 0xff000000 // This won't compile

  // But I can say
  case class OverlayFilter(c: Color)
  val fadeLayer = OverlayFilter(halfTransparency)

  // Then why an opaque type is useful???? Because if we combine opaque types with:
  // 1. companion object of the type
  // and/or
  // 2. extension methods
  // we can control exactly WHICH API that type will expose to the outside world. Examples:

  // 1. companion object
  object SocialNetwork {
    opaque type Name = String

    object Name {
      def apply(str: String): Name = str
    }

    extension (name: Name)
      def length(): Int =
        name.length() // This calls the String API length method
  }

  // usage
  import SocialNetwork.*

  val myName = Name("Diego")
  println(myName.length())
}
