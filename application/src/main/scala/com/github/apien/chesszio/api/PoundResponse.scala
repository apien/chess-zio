package com.github.apien.chesszio.api

import com.github.apien.chesszio.engine.PieceType
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class PoundResponse(position:Position, kind: PieceType)

object PoundResponse {
  given decoder : JsonDecoder[PoundResponse] = DeriveJsonDecoder.gen[PoundResponse]
  given encoder : JsonEncoder[PoundResponse] = DeriveJsonEncoder.gen[PoundResponse]
}
