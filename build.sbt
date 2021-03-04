name := "search"

version := "0.1"

scalaVersion := "2.13.5"

//fork in console := true

val ScalatestVersion = "3.2.2"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % ScalatestVersion % Test
)
