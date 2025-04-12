package com.github.apien.chesszio.config

import com.typesafe.config.{Config, ConfigFactory}
import pureconfig.ConfigObjectSource
import pureconfig.ConfigSource
import pureconfig.*

case class KafkaConfiguration(address: String, topic: String)

case class ApplicationConfiguration(actionsKafkaProducer: KafkaConfiguration)

given ConfigReader[ApplicationConfiguration] = ConfigReader.derived

object ApplicationConfiguration {

  def loadOrThrow(): ApplicationConfiguration = {
    ConfigFactory.invalidateCaches()

    val config: Config = ConfigFactory
      .systemEnvironment()
      .withFallback(ConfigFactory.systemProperties())
      .withFallback(ConfigFactory.defaultApplication())
      .resolve()

    val configSource: ConfigObjectSource = ConfigSource.fromConfig(config.getConfig("application"))
    configSource.loadOrThrow[ApplicationConfiguration]
  }
}
