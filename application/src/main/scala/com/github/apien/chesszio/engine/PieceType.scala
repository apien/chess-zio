package com.github.apien.chesszio.engine

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

enum PieceType {
   case Rok, Bishop
}

object PieceType {
  given  encoder : JsonDecoder[PieceType] = DeriveJsonDecoder.gen[PieceType]
  given  decoder : JsonEncoder[PieceType] = DeriveJsonEncoder.gen[PieceType]
}
