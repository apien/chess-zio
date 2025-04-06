import sbt._
import sbt.librarymanagement.ModuleID

object Dependencies {
  private val tapirVersion = "1.11.23"

  private val config = Seq(
    "com.typesafe"           % "config"          % "1.4.3",
    "com.github.pureconfig" %% "pureconfig-core" % "0.17.8"
  )

  lazy val zio: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio"       % "2.1.17",
    "dev.zio" %% "zio-kafka" % "2.7.4"
  )

  lazy val tapir: Seq[ModuleID] = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-zio"          % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion
  )

  val application: Seq[ModuleID] = zio ++ tapir
  val client: Seq[ModuleID]      = config ++ zio
}
