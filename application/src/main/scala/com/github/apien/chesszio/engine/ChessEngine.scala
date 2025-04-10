package com.github.apien.chesszio.engine

import com.github.apien.chesszio.engine
import com.github.apien.chesszio.engine.move.MoveError.PieceDoesNotExist
import com.github.apien.chesszio.engine.move.{MoveError, MoveValidator}

class ChessEngine(private val board: Board) {

  def move(id: PieceId, destination: Square): Either[MoveError, ChessEngine] = {
    val validateMove: Either[MoveError, (Square, Piece)] = board
      .findPieceById(id)
      .fold(Left(PieceDoesNotExist)) { case (pieceSquare, piece) =>
        val moveValidator = MoveValidator.get(piece.kind)
        moveValidator
          .validate(pieceSquare, destination, board)
          .map(_ => (pieceSquare, piece))
      }

    validateMove
      .map { case (square, _) =>
        val updatedBoard = board.move(square, destination)
        ChessEngine(updatedBoard)
      }
  }

  def getState: Map[Square, Piece] = board.getBoardState
}

object ChessEngine {

  def build(pieces: Map[Square, Piece]) = ChessEngine(Board(pieces))

}
