package com.github.apien.chesszio.api

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Position(row: Int, column: Int)

object Position {
  given decoder : JsonDecoder[Position] = DeriveJsonDecoder.gen[Position]
  given encoder : JsonEncoder[Position] = DeriveJsonEncoder.gen[Position]
}
