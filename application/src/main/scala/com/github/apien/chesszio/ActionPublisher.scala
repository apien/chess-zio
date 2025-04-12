package com.github.apien.chesszio

import com.github.apien.chesszio.config.ApplicationConfiguration
import org.apache.kafka.clients.producer.ProducerRecord
import zio.kafka.producer.*
import zio.kafka.serde.*
import zio.{Task, ZIO, ZLayer}

trait ActionPublisher {

  def produce[A <: Action](action: A): Task[Unit]
}

class KafkaActionPublisher(producer: Producer, topic: String) extends ActionPublisher {

  def produce[A <: Action](action: A): Task[Unit] = {
    val producerRecord: ProducerRecord[String, Action] = new ProducerRecord(topic, action.gameId, action)
    producer
      .produce(
        producerRecord,
        keySerializer = Serde.string,
        valueSerializer = Action.serde
      )
      .unit
  }

}

object KafkaActionPublisher {
  val live: ZLayer[ApplicationConfiguration, Throwable, ActionPublisher] = ZLayer.scoped {
    for {
      configuration <- ZIO.service[ApplicationConfiguration]
      kafkaSettings = configuration.actionsKafkaProducer
      producer <- Producer.make(ProducerSettings.apply(List(kafkaSettings.address)))
    } yield new KafkaActionPublisher(producer, kafkaSettings.topic)
  }
}
