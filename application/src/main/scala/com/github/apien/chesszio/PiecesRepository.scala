package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.SquareOccupied
import com.github.apien.chesszio.engine.{GameId, Piece, PieceId, PieceType, Square}
import zio.{IO, Task, ZLayer}

trait PiecesRepository {

  def getPiecesByGameId(gameId: GameId): Task[Map[Square, Piece]]

  def storeGameState(gameId: GameId, pieces: Map[Square, Piece]): Task[Unit]

  def addPiece(gameId: GameId, id: PieceId, pieceType: PieceType, square: Square): IO[SquareOccupied, Unit]

  def removePiece(gameId: GameId, pieceId: PieceId): Task[Unit]

}

class MemoryPiecesRepository extends PiecesRepository {
  override def getPiecesByGameId(gameId: GameId): Task[Map[Square, Piece]] = ???

  override def storeGameState(gameId: GameId, pieces: Map[Square, Piece]): Task[Unit] = ???

  override def addPiece(gameId: GameId, id: PieceId, pieceType: PieceType, square: Square): IO[SquareOccupied, Unit] =
    ???

  override def removePiece(gameId: GameId, pieceId: PieceId): Task[Unit] = ???
}

object MemoryPiecesRepository {
  val live = ZLayer.succeed(MemoryPiecesRepository())
}
