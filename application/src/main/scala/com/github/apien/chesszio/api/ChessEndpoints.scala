package com.github.apien.chesszio.api

import com.github.apien.chesszio.api.ErrorInfo.*
import com.github.apien.chesszio.api.model.{CreatePoundApiRequest, PieceWithSquare}
import com.github.apien.chesszio.engine.*
import sttp.model.StatusCode
import sttp.tapir.SchemaType.SInteger
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.ztapir.*
import sttp.tapir.{Endpoint, EndpointOutput, Schema, endpoint}
import zio.{ULayer, ZLayer}

class ChessEndpoints {

  val defaultErrorOutputs: EndpointOutput.OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
    oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[BadRequest])),
    oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound])),
    oneOfVariant(statusCode(StatusCode.Conflict).and(jsonBody[Conflict])),
    oneOfVariant(statusCode(StatusCode.UnprocessableEntity).and(jsonBody[ValidationFailed])),
    oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[InternalServerError]))
  )

  // TODO I can not force iron-tapir to derive schema.
  implicit given Schema[Row] = Schema(SInteger())
  implicit given Schema[Column] = Schema(SInteger())

  private val baseGameEndpoint: Endpoint[Unit, GameId, ErrorInfo, Unit, Any] = endpoint
    .errorOut(defaultErrorOutputs)
    .in("api" / "games" / path[GameId].name("gameId"))

  private val basePoundEndpoint = baseGameEndpoint.in("pieces")

  val createPieceEndpoint: Endpoint[Unit, (GameId, CreatePoundApiRequest), ErrorInfo, PieceWithSquare, Any] =
    basePoundEndpoint
      .summary("Put a new piece on a board")
      .post
      .in(jsonBody[CreatePoundApiRequest])
      .out(jsonBody[PieceWithSquare])

  val movePieceEndpoint: Endpoint[Unit, (GameId, PieceId, Square), ErrorInfo, Unit, Any] =
    basePoundEndpoint.put
      .summary("Move a piece")
      .in(path[PieceId].name("pieceId"))
      .in(jsonBody[Square])
      .out(statusCode(StatusCode.NoContent))

  val deletePieceEndpoint: Endpoint[Unit, (GameId, PieceId), ErrorInfo, Unit, Any] =
    basePoundEndpoint
      .summary("Mark a piece as deleted")
      .in(path[PieceId].name("pieceId"))
      .delete

  val getPieceEndpoint: Endpoint[Unit, (GameId, PieceId), ErrorInfo, PieceWithSquare, Any] =
    basePoundEndpoint
      .summary("Get a select piece")
      .in(path[PieceId].name("pieceId"))
      .get
      .out(jsonBody[PieceWithSquare])
}

object ChessEndpoints {
  val live: ULayer[ChessEndpoints] = ZLayer.succeed(new ChessEndpoints())
}
