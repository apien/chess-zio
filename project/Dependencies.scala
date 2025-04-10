import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {
  private val tapirVersion = "1.11.23"

  private val config = Seq(
    "com.typesafe"           % "config"          % "1.4.3",
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.8"
  )

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"               % "2.1.17",
    "dev.zio" %% "zio-kafka"         % "2.7.4",
    "dev.zio" %% "zio-test"          % "2.1.17" % Test,
    "dev.zio" %% "zio-test-sbt"      % "2.1.17" % Test,
    "dev.zio" %% "zio-test-magnolia" % "2.1.17" % Test
  )

  lazy val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
  )

  lazy val iron: Seq[ModuleID] = Seq(
    "io.github.iltotore" %% "iron" % "3.0.0"
  )

  val application: Seq[ModuleID] = zio ++ tapir ++ iron
  val client: Seq[ModuleID]      = config ++ zio
}
