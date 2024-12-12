package com.example.scala3.advanced.typesystem

import scala.util.Try

object HigherKindedTypes {

  // A higher kinded type is a type that receives other types to make some operations.
  // For example, let's say you're creating a library that deals with multiplying by 10 elements of
  // lists, or options, or tries:
  def do10xList(list: List[Int]): List[Int] = list.map(_ * 10)
  def do10xOption(option: Option[Int]): Option[Int] = option.map(_ * 10)
  def do10xTry(aTry: Try[Int]): Try[Int] = aTry.map(_ * 10)

  // Then we're tempted to create an abstract type that takes any type (DRY).
  // step 1: Type Class (TC) definition:
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // step 2: TC instance
  given listFunctor: Functor[List] with
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)

  // step 3: "user-facing" API
  def do10x[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    functor.map(container)(_ * 10)

  println(do10x(List(1, 2, 3)))

  // step 4: with extension methods
  // we can define an extension method that will be used by the implementation
  extension [F[_], A](container: F[A])(using functor: Functor[F])
    def map[B](f: A => B): F[B] = functor.map(container)(f)

  // so that:
  def do10x_v2[F[_]](container: F[Int])(using functor: Functor[F]): F[Int] =
    container.map(_ * 10)
  // or
  def do10x_v3[F[_]: Functor](container: F[Int]): F[Int] = container.map(_ * 10)
}
