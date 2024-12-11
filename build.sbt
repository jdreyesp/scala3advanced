val scala3Version = "3.5.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala3-advanced-features",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3"
    )
  )
