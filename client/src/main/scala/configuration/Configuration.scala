package configuration

import com.typesafe.config.Config
import pureconfig.ConfigObjectSource
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.derivation.default._

case class Configuration(
  kafka: KafkaConfiguration
)
case class KafkaConfiguration(
  address: String,
  topic: String,
  group: String,
  client: String
)

given ConfigReader[Configuration] = ConfigReader.derived

object Configuration {
  def apply(config: Config): Configuration = {
    val configSource: ConfigObjectSource = ConfigSource.fromConfig(config.getConfig("client"))
    configSource.loadOrThrow[Configuration]
  }
}
