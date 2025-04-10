package com.github.apien.chesszio.api

import com.github.apien.chesszio.PieceSquare
import com.github.apien.chesszio.api.ErrorInfo.*
import com.github.apien.chesszio.engine.{GameId, Piece, PieceId, Row, Square}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.zioJson.given
import sttp.model.StatusCode
import sttp.tapir.codec.iron.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.ztapir.*
import sttp.tapir.{Endpoint, EndpointOutput, endpoint}
import zio.json.*
import zio.{ULayer, ZLayer}

class ChessEndpoints {

  val defaultErrorOutputs: EndpointOutput.OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
    oneOfVariant(statusCode(StatusCode.BadRequest).and(jsonBody[BadRequest])),
    oneOfVariant(statusCode(StatusCode.NotFound).and(jsonBody[NotFound])),
    oneOfVariant(statusCode(StatusCode.Conflict).and(jsonBody[Conflict])),
    oneOfVariant(statusCode(StatusCode.UnprocessableEntity).and(jsonBody[ValidationFailed])),
    oneOfVariant(statusCode(StatusCode.InternalServerError).and(jsonBody[InternalServerError]))
  )

  private val baseGameEndpoint: Endpoint[Unit, GameId, ErrorInfo, Unit, Any] = endpoint
    .errorOut(defaultErrorOutputs)
    .in("api" / "games" / path[GameId])

  private val basePoundEndpoint = baseGameEndpoint.in("pieces")

  val createPieceEndpoint =
    basePoundEndpoint.post
      .in(jsonBody[CreatePoundRequest])
      .out(jsonBody[Piece])

  val movePieceEndpoint: Endpoint[Unit, (GameId, PieceId, Position), ErrorInfo, Unit, Any] =
    basePoundEndpoint.put
      .in(path[PieceId].name("pieceId"))
      .in(jsonBody[Position])

  val deletePieceEndpoint: Endpoint[Unit, (GameId, PieceId), ErrorInfo, Unit, Any] =
    basePoundEndpoint
      .in(path[PieceId].name("pieceId"))
      .delete

  val getPiece: Endpoint[Unit, (GameId, PieceId), ErrorInfo, Piece, Any] =
    basePoundEndpoint
      .in(path[PieceId].name("pieceId"))
      .get
      .out(jsonBody[Piece])


//    val fooEndpoint =
//      basePoundEndpoint.post
//        .in(jsonBody[CreatePoundRequest])
//        .out(jsonBody[Row])

}

object ChessEndpoints {
  val live: ULayer[ChessEndpoints] = ZLayer.succeed(new ChessEndpoints())
}
