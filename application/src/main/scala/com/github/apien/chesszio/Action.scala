package com.github.apien.chesszio

import com.github.apien.chesszio.engine.{GameId, PieceId, PieceType, Square}
import zio.ZIO
import zio.json.{DecoderOps, DeriveJsonDecoder, DeriveJsonEncoder, EncoderOps, JsonDecoder, JsonEncoder}
import zio.kafka.serde.*
import zio.json._
import java.util.UUID

opaque type ActionId = UUID

object ActionId {
  given encoder: JsonEncoder[ActionId] = JsonEncoder.string.contramap(_.toString)
  given decoder: JsonDecoder[ActionId] = JsonDecoder.string.map(UUID.fromString)

  def generate(): ActionId = UUID.randomUUID()

}

sealed trait Action {
  def actionId: ActionId
  def gameId: GameId
}

object Action {
  case class PieceMoved(actionId: ActionId, gameId: GameId, pieceId: PieceId, destination: Square) extends Action

  case class PieceCreated(actionId: ActionId, gameId: GameId, pieceId: PieceId, pieceType: PieceType, square: Square)
      extends Action

  case class PieceRemoved(actionId: ActionId, gameId: GameId, pieceId: PieceId) extends Action

  given decoder: JsonDecoder[Action] = DeriveJsonDecoder.gen[Action]
  given encoder: JsonEncoder[Action] = DeriveJsonEncoder.gen[Action]
  val serde: Serde[Any, Action] = Serde.string.inmapZIO[Any, Action](s =>
    ZIO
      .fromEither(s.fromJson[Action])
      .mapError(e => new RuntimeException(e))
  )(r => ZIO.succeed(r.toJson))
}
