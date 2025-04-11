package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.{PieceDoesNotExist, SquareOccupied}
import com.github.apien.chesszio.engine.move.MoveError
import com.github.apien.chesszio.engine.*
import zio.{IO, UIO, ZIO, ZLayer}

import java.util.UUID

class ChessService(piecesRepository: PiecesRepository) {

  def addPiece(gameId: GameId, pieceType: PieceType, square: Square): IO[SquareOccupied, Piece] = {
    val id = UUID.randomUUID().toString
    piecesRepository
      .addPiece(gameId, id, pieceType = pieceType, square = square)
      .as(Piece(id, pieceType, false))
  }

  def getPiece(gameId: GameId, pieceId: PieceId): UIO[Option[(Square, Piece)]] =
    piecesRepository.getPiece(gameId, pieceId)

  def move(gameId: GameId, pieceId: PieceId, destination: Square): IO[MoveError, Unit] = for {
    pieces <- piecesRepository.getPiecesByGameId(gameId)
    engine = ChessEngine.build(pieces)
    moveResult <- ZIO.fromEither(engine.move(pieceId, destination))
    _          <- piecesRepository.storeGameState(gameId, moveResult.getState)
  } yield ()

  def removePiece(gameId: GameId, pieceId: PieceId): IO[PieceDoesNotExist, Unit] =
    piecesRepository.removePiece(gameId, pieceId)

}

object ChessService {
  case object SquareOccupied
  type SquareOccupied = SquareOccupied.type
  case object PieceDoesNotExist
  type PieceDoesNotExist = PieceDoesNotExist.type

  val live: ZLayer[PiecesRepository, Nothing, ChessService] = ZLayer.fromFunction(new ChessService(_))
}
