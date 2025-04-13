package com.github.apien.chesszio.api

import com.github.apien.chesszio.ChessService
import com.github.apien.chesszio.ChessService.{PieceDoesNotExist, SquareOccupied}
import com.github.apien.chesszio.api.model.{CreatePoundApiRequest, PieceWithSquare}
import com.github.apien.chesszio.engine.move.MoveError
import sttp.tapir.ztapir.*
import zio.*

class ChessRoutes(chessEndpoints: ChessEndpoints, chessService: ChessService) {

  // TODO Implement the logic
  val createPieceRoute: ZServerEndpoint[Any, Any] = chessEndpoints.createPieceEndpoint
    .zServerLogic { case (gameId, CreatePoundApiRequest(square, pieceType)) =>
      chessService
        .addPiece(gameId, pieceType, square)
        .foldZIO(
          { case SquareOccupied => ZIO.fail(ErrorInfo.Conflict("Requested square is occupied already")) },
          piece => ZIO.succeed(PieceWithSquare(piece, square))
        )
    }

  val movePoundRoute: ZServerEndpoint[Any, Any] = chessEndpoints.movePieceEndpoint
    .zServerLogic { case (gameId, pieceId, destinationSquare) =>
      chessService
        .move(gameId, pieceId, destinationSquare)
        .mapError {
          case MoveError.IllegalMove =>
            ErrorInfo.Conflict("No allowed move: select piece can not move this way or not free path")
          case MoveError.PieceDoesNotExist => ErrorInfo.NotFound(s"Piece $pieceId in game $gameId does not exist")
          case MoveError.DestinationSquareOccupied => ErrorInfo.Conflict(s"Selected square is not free")
        }
    }

  val deletePieceRoute: ZServerEndpoint[Any, Any] = chessEndpoints.deletePieceEndpoint.zServerLogic {
    case (gameId, pieceId) =>
      chessService
        .removePiece(gameId, pieceId)
        .mapError { case PieceDoesNotExist => ErrorInfo.NotFound(s"Piece $pieceId does not exist in game $gameId") }
  }

  val getPieceRoute: ZServerEndpoint[Any, Any] = chessEndpoints.getPieceEndpoint.zServerLogic {
    case (gameId, pieceId) =>
      chessService
        .getPiece(gameId, pieceId)
        .some
        .mapBoth(
          { case None => ErrorInfo.NotFound(s"Piece $pieceId in game $gameId does not exist"): ErrorInfo },
          { case (square, piece) => PieceWithSquare(piece, square) }
        )

  }

  val routes = List(createPieceRoute, movePoundRoute, deletePieceRoute, getPieceRoute)

}

object ChessRoutes {
  val live: ZLayer[ChessEndpoints & ChessService, Nothing, ChessRoutes] = ZLayer.fromFunction(new ChessRoutes(_, _))
}
