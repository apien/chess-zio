ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.4"

lazy val application = (project in file("application"))
  .settings(
    name := "application",
    libraryDependencies ++= Dependencies.application,
    scalacOptions ++= Seq("-Yretain-trees")
  )

lazy val client = (project in file("client"))
  .settings(
    name := "client",
    libraryDependencies ++= Dependencies.client
  )

lazy val root = (project in file("."))
  .aggregate(application, client)
  .settings(
    name := "chess-zio",
    run := (application / Compile / run).evaluated,
    Global / cancelable := false
  )
