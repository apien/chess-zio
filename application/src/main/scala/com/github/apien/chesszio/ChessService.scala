package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.SquareOccupied
import com.github.apien.chesszio.api.Position
import com.github.apien.chesszio.engine.{GameId, Piece, PieceId, PieceType, Square}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}
import zio.{IO, Task, ZIO, ZLayer}

import java.util.UUID

class ChessService(piecesRepository: PiecesRepository) {

  def addPiece(gameId: GameId, pieceType: PieceType, square: Square): IO[SquareOccupied, Piece] = {
    val id = UUID.randomUUID().toString
    piecesRepository
      .addPiece(gameId, id, pieceType = pieceType, square = square)
      .as(Piece(id, pieceType, false))
  }

  def getPiece(gameId: GameId, pieceId: PieceId): IO[Nothing, Option[PieceSquare]] = ZIO.none

}

object ChessService {
  case object SquareOccupied
  type SquareOccupied = SquareOccupied.type

  val live: ZLayer[PiecesRepository, Nothing, ChessService] = ZLayer.fromFunction(new ChessService(_))
}

case class PieceSquare(piece: Piece, square: Square)

object PieceSquare {
  given encoder: JsonEncoder[PieceSquare] = DeriveJsonEncoder.gen[PieceSquare]
  given decoder: JsonDecoder[PieceSquare] = DeriveJsonDecoder.gen[PieceSquare]
}
