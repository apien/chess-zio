import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {
  private val tapirVersion = "1.11.23"
  private val ironVersion  = "3.0.0"
  private val zioVersion   = "2.1.17"

  private val config = Seq(
    "com.typesafe"           % "config"          % "1.4.3",
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.8"
  )

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"               % zioVersion,
    "dev.zio" %% "zio-kafka"         % "2.12.0",
    "dev.zio" %% "zio-streams"       % zioVersion,
    "dev.zio" %% "zio-test"          % zioVersion % Test,
    "dev.zio" %% "zio-test-sbt"      % zioVersion % Test,
    "dev.zio" %% "zio-test-magnolia" % zioVersion % Test
  )

  lazy val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
  )

  lazy val iron: Seq[ModuleID] = Seq(
    "io.github.iltotore"          %% "iron"          % ironVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-iron"    % "1.11.24",
    "io.github.iltotore"          %% "iron-zio-json" % ironVersion
  )

  val application: Seq[ModuleID] = config ++ zio ++ tapir ++ iron
  val client: Seq[ModuleID]      = config ++ zio
}
