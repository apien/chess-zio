package com.github.apien.chesszio.api

import com.github.apien.chesszio.ChessService
import com.github.apien.chesszio.api.ErrorInfo.BadRequest
import com.github.apien.chesszio.api.{ChessEndpoints, CreatePoundRequest, Position, PoundResponse}
import com.github.apien.chesszio.engine.{Piece, PieceId}
import sttp.tapir.ztapir.*
import zio.*

import scala.util.chaining.*
class ChessRoutes(chessEndpoints: ChessEndpoints, chessService: ChessService) {

//  def defaultErrorsMappings[E <: Throwable, A](io: IO[E, A]): IO[ErrorInfo, A] = io.mapError {
//    case e: Exceptions.AlreadyInUse => Conflict(e.message)
//    case e: Exceptions.NotFound     => NotFound(e.message)
//    case e: Exceptions.BadRequest   => BadRequest(e.message)
//    case e: Exceptions.Unauthorized => Unauthorized(e.message)
//    case _                          => InternalServerError()
//  }

  // TODO Implement the logic
  val createPoundRooute: ZServerEndpoint[Any, Any] = chessEndpoints.createPieceEndpoint
    .zServerLogic { case (_, CreatePoundRequest(position, kind)) =>
      ZIO.succeed(Piece(PieceId.random(), kind, false))
    }

  val movePoundRoute: ZServerEndpoint[Any, Any] = chessEndpoints.movePieceEndpoint
    .zServerLogic { case (_, _, position: Position) =>
      ZIO.unit
    }

  val deletePoundRoute: ZServerEndpoint[Any, Any] = chessEndpoints.deletePieceEndpoint.zServerLogic {
    case (_, poundId) =>
      ZIO.unit
  }

  val getPieceRoute: ZServerEndpoint[Any, Any] = chessEndpoints.getPiece.zServerLogic { case (gameId, pieceId) =>
    chessService
      .getPiece(gameId, pieceId)
      .flatMap {
        case None              => ZIO.fail(ErrorInfo.NotFound())
        case Some(pieceSquare) => ZIO.succeed(pieceSquare.piece)
      }

  }

  val routes = List(createPoundRooute, movePoundRoute, deletePoundRoute, getPieceRoute)

}

object ChessRoutes {
  val live: ZLayer[ChessEndpoints & ChessService, Nothing, ChessRoutes] = ZLayer.fromFunction(new ChessRoutes(_, _))
}
