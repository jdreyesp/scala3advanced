package com.example.scala3.advanced.typesystem

object LiteralTypes extends App {

  // 1 - Literal types
  // I can have literal types that they compiler will infer as a subtype of a primitive type, e.g.
  val three: 3 = 3 // This will infer 3 type as Int since 3 <: Int

  // Same for double, boolean and string
  val pi: 3.14 = 3.14
  val truth: true = true
  val str: "Scala" = "Scala"

  // Also as arguments of methods for instance
  // Note that 42 is a type here
  def doSomethingWithYourLife(meaning: Option[42]) = meaning.foreach(println)

}

object UnionTypes extends App {
  def ambivalentMethod(arg: String | Int) = arg match {
    case _: String => "this is a string"
    case _: Int    => "this is an int"
  } // pattern matching is complete here

  // Problem: when defining a variable as a result of an expression, the compiler uses the LCA (lowest common ancestor)
  // approach. E.g.:
  val myVar = if (42 > 0) 42 else "hello" // Note: myVar is of type 'Any'
  val myVarv2: String | Int = if (42 > 0) 42 else "hello" // type String|Int

  // this is a good decision from the compiler team in scala since it's preferrable to use a supertype instead of
  // a synthetised type to allow compatibility for more code blocks.
  // We need to explicitly declare String | Int if we want to use union types as the variable type.
}

object FlowTyping extends App {
  // Flow-typing is the ability of the compiler on inferring the type of union-typed-variable based on where the
  // code block is located. e.g.:

  type Maybe[T] = T | Null

  def handleMaybe(maybe: Maybe[String]): Int =
    // At this point, the compiler knows that if maybe is not of type null, then
    // it has to be of type String
    if (maybe != null) maybe.length
    else 0
}

object IntersectionTypes extends App {

  // Intersection types denote multiple types in a variable. However, we have to be careful when
  // in a diamond problem situation (see diamond problem in AdvancedInheritance.scala),
  // since by just defining the intersection type won't be enough to say which of the
  // overloaded methods will be called.
  // Example:

  trait Gadget {
    def use(): Unit
  }

  trait Camera extends Gadget {
    def takePhoto(): Unit = println("smile!")
    override def use(): Unit = println("snap")
  }

  trait Phone extends Gadget {
    def makePhoneCall(): Unit = println("Calling...")
    override def use(): Unit = println("ring")
  }

  def useSmartDevice(sp: Camera & Phone): Unit = {
    sp.takePhoto()
    sp.makePhoneCall()
    sp.use() // which use will be used here? It depends on how the smart device is defined, see above
  }

  class SmartPhone extends Phone with Camera
  class CameraWithPhone extends Camera with Phone

  useSmartDevice(
    new SmartPhone
  ) // This will use "snap" (see diamond problem in AdvancedInheritance.scala)
  useSmartDevice(
    new CameraWithPhone
  ) // This will use "ring" (see diamond problem in AdvancedInheritance.scala)

  // Combination of intersection types and covariance
  trait HostConfig
  trait HostController {
    def get: Option[HostConfig]
  }

  trait PortConfig
  trait PortController {
    def get: Option[PortConfig]
  }

  // I can say
  def getConfigs(controller: HostController & PortController) = controller.get

  // Why this compiles? It's supposed to fail since get operations return different types!
  // Since controller is of type HostController & PortController, the compiler assumes that your return type
  // will be Option[HostConfig] & Option[PortConfig]. Since Option is COVARIANT (class Option[+T]), then:
  // Option[HostConfig] & Option[PortConfig] can be expressed as Option[HostConfig & PortConfig], and that's precisely
  // the type that's inferred by the compiler.

  // This gives us the power of mixing up different return types when calling an intersected-type-variable.
  // Use this wisely since this is quite powerful yet very magical in terms of code complexity.
}
