package com.github.apien.chesszio.api.model

import com.github.apien.chesszio.engine.{Piece, Square}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class PieceWithSquare(piece: Piece, square: Square)

object PieceWithSquare {
  given encoder: JsonEncoder[PieceWithSquare] = DeriveJsonEncoder.gen[PieceWithSquare]
  given decoder: JsonDecoder[PieceWithSquare] = DeriveJsonDecoder.gen[PieceWithSquare]
}
