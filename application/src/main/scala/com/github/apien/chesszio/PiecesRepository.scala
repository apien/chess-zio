package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.{PieceDoesNotExist, SquareOccupied}
import com.github.apien.chesszio.MemoryPiecesRepository.{GamePieceKey, PieceSquareDb}
import com.github.apien.chesszio.engine.*
import zio.{IO, Ref, UIO, ZLayer}

trait PiecesRepository {

  def getPiecesByGameId(gameId: GameId): UIO[Map[Square, Piece]]

  def getActivePiecesByGameId(gameId: GameId): UIO[Map[Square, Piece]]

  def getPiece(gameId: GameId, pieceId: PieceId): UIO[Option[(Square, Piece)]]

  def storeGameState(gameId: GameId, pieces: Map[Square, Piece]): UIO[Unit]

  def addPiece(gameId: GameId, id: PieceId, pieceType: PieceType, square: Square): IO[SquareOccupied, Unit]

  def removePiece(gameId: GameId, pieceId: PieceId): IO[PieceDoesNotExist, Unit]

}

class MemoryPiecesRepository(acc: Ref[Map[GamePieceKey, PieceSquareDb]]) extends PiecesRepository {

  def getAll: UIO[Map[GamePieceKey, PieceSquareDb]] = acc.get

  override def getPiecesByGameId(gameId: GameId): UIO[Map[Square, Piece]] =
    acc.get.map(_.filter(_._2.gameId == gameId).map(_._2.toDomain))

  override def getActivePiecesByGameId(gameId: GameId): UIO[Map[Square, Piece]] =
    getPiecesByGameId(gameId).map(_.filterNot(_._2.deleted))

  override def getPiece(gameId: GameId, pieceId: PieceId): UIO[Option[(Square, Piece)]] =
    acc.get.map(_.find(_._2.pieceId == pieceId).map(_._2.toDomain))

  override def storeGameState(gameId: GameId, pieces: Map[Square, Piece]): UIO[Unit] =
    acc.modify { initial =>
      val otherGamesOrRemoved = initial.filter { case (GamePieceKey(gId, pId), piece) =>
        (gId == gameId && piece.deleted) || gId != gameId
      }
      val resultMap = otherGamesOrRemoved ++ pieces.map { case (square, piece) =>
        (GamePieceKey(gameId, piece.id), PieceSquareDb.from(gameId, piece, square))
      }

      ((), resultMap)
    }

  override def addPiece(gameId: GameId, id: PieceId, pieceType: PieceType, square: Square): IO[SquareOccupied, Unit] = {
    val key = GamePieceKey(gameId, id)
    acc
      .modify[Either[SquareOccupied, Unit]] { currentState =>
        val foundPiece = currentState.find { case (key, piece) => key.gameId == gameId && piece.square == square }
        foundPiece match {
          case Some(_) => (Left(SquareOccupied), currentState)
          case _ => {
            val resultMap = currentState ++ Map(key -> PieceSquareDb.from(gameId, id, pieceType, false, square))
            (Right(()), resultMap)
          }
        }
      }
      .absolve
  }

  override def removePiece(gameId: GameId, pieceId: PieceId): IO[PieceDoesNotExist, Unit] = acc.modify { currentState =>
    val foundPiece = currentState.find { case (GamePieceKey(gameId, _), piece) =>
      gameId == gameId && piece.pieceId == pieceId
    }

    foundPiece match {
      case Some((key, _)) => {
        val updatedState = currentState.updatedWith(key)(_.map(_.copy(deleted = true)))
        (Right(()), updatedState)
      }
      case None => (Left(PieceDoesNotExist), currentState)
    }
  }.absolve

}

object MemoryPiecesRepository {
  val live: ZLayer[Any, Nothing, PiecesRepository] =
    ZLayer.fromZIO(Ref.make(Map.empty[GamePieceKey, PieceSquareDb])) >>> ZLayer.fromFunction(
      MemoryPiecesRepository(_)
    )

  case class GamePieceKey(gameId: GameId, pieceId: PieceId)

  case class PieceSquareDb(
    gameId: GameId,
    pieceId: PieceId,
    kind: PieceType,
    deleted: Boolean,
    square: Square
  ) {
    def toDomain: (Square, Piece) = (square, Piece(pieceId, kind, deleted))
  }

  object PieceSquareDb {
    def from(gameId: GameId, pieceId: PieceId, pieceType: PieceType, deleted: Boolean, square: Square): PieceSquareDb =
      PieceSquareDb(gameId, pieceId, pieceType, deleted, square)

    def from(gameId: GameId, piece: Piece, square: Square): PieceSquareDb =
      PieceSquareDb(gameId, piece.id, piece.kind, piece.deleted, square)
  }
}
