package com.github.apien.chesszio.api

import com.github.apien.chesszio.engine.PieceType
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class CreatePoundRequest(position: Position, kind: PieceType)

object CreatePoundRequest {
  given decoder : JsonDecoder[CreatePoundRequest] = DeriveJsonDecoder.gen[CreatePoundRequest]
  given encoder : JsonEncoder[CreatePoundRequest] = DeriveJsonEncoder.gen[CreatePoundRequest]
}
