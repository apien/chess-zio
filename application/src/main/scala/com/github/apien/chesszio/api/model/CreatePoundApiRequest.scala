package com.github.apien.chesszio.api.model

import com.github.apien.chesszio.engine.{PieceType, Square}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class CreatePoundApiRequest(position: Square, kind: PieceType)

object CreatePoundApiRequest {
  given decoder: JsonDecoder[CreatePoundApiRequest] = DeriveJsonDecoder.gen[CreatePoundApiRequest]
  given encoder: JsonEncoder[CreatePoundApiRequest] = DeriveJsonEncoder.gen[CreatePoundApiRequest]
}
