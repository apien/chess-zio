package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.{PieceDoesNotExist, SquareOccupied}
import Action.{PieceCreated, PieceMoved, PieceRemoved}
import com.github.apien.chesszio.engine.move.MoveError
import com.github.apien.chesszio.engine.*
import com.github.apien.chesszio.engine.PieceType.{Bishop, Rok}
import zio.{IO, UIO, ZIO, ZLayer}

import java.util.UUID

class ChessService(piecesRepository: PiecesRepository, actionRepository: ActionRepository) {

  def addPiece(gameId: GameId, pieceType: PieceType, square: Square): IO[SquareOccupied, Piece] = {
    // TODO create a function in the companion object
    val pieceId = UUID.randomUUID().toString
    for {
      _ <- piecesRepository.addPiece(gameId, pieceId, pieceType, square = square)
      _ <- actionRepository.store(PieceCreated(ActionId.generate(), gameId, pieceId, pieceType, square))
    } yield Piece(pieceId, pieceType, false)
  }

  def getPiece(gameId: GameId, pieceId: PieceId): UIO[Option[(Square, Piece)]] =
    piecesRepository.getPiece(gameId, pieceId)

  def move(gameId: GameId, pieceId: PieceId, destination: Square): IO[MoveError, Unit] = for {
    pieces <- piecesRepository.getPiecesByGameId(gameId)
    engine = ChessEngine.build(pieces)
    moveResult <- ZIO.fromEither(engine.move(pieceId, destination))
    _          <- piecesRepository.storeGameState(gameId, moveResult.getState)
    _          <- actionRepository.store(PieceMoved(ActionId.generate(), gameId, pieceId, destination))
  } yield ()

  def removePiece(gameId: GameId, pieceId: PieceId): IO[PieceDoesNotExist, Unit] =
    for {
      _ <- piecesRepository.removePiece(gameId, pieceId)
      _ <- actionRepository.store(PieceRemoved(ActionId.generate(), gameId, pieceId))
    } yield ()

}

object ChessService {
  case object SquareOccupied
  type SquareOccupied = SquareOccupied.type
  case object PieceDoesNotExist
  type PieceDoesNotExist = PieceDoesNotExist.type

  val live: ZLayer[PiecesRepository & ActionRepository, Nothing, ChessService] =
    ZLayer.fromFunction(new ChessService(_, _))
}
