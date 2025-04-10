package com.github.apien.chesszio.engine

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.util.UUID

type PieceId = String
type GameId  = String

case class Piece(id: PieceId, kind: PieceType, deleted: Boolean)

object Piece {
  given decoder: JsonDecoder[Piece] = DeriveJsonDecoder.gen[Piece]
  given encoder: JsonEncoder[Piece] = DeriveJsonEncoder.gen[Piece]
}

object PieceId {
  def fromString(source: String): PieceId = source
  def random(): PieceId                   = UUID.randomUUID().toString
  given decoder: JsonDecoder[PieceId] = JsonDecoder[String].map(PieceId.fromString)
  given encoder: JsonEncoder[PieceId] = JsonEncoder[String].contramap(id => id)
//  given codec :PlainCodec[PieceId] = Codec.string.mapDecode(input =>DecodeResult.Value(PieceId.fromString(input)))(id => id)
}

object GameId {
  def fromString(source: String): GameId = source
  given decoder: JsonDecoder[GameId] = JsonDecoder[String].map(GameId.fromString)
  given encoder: JsonEncoder[GameId] = JsonEncoder[String].contramap(id => id)
//  given codec :PlainCodec[GameId] = Codec.string.mapDecode(input =>DecodeResult.Value(GameId.fromString(input)))(id => id)
}
