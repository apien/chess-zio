package com.github.apien.chesszio.engine.move

import com.github.apien.chesszio.engine
import com.github.apien.chesszio.engine.move.MoveError.{DestinationSquareOccupied, IllegalMove}
import com.github.apien.chesszio.engine.{Board, PieceType, Square}

trait MoveValidator {

  def validate(source: Square, destination: Square, board: Board): Either[MoveError, Unit]

}

object MoveValidator {
  def get(pieceType: PieceType): MoveValidator = pieceType match {
    case engine.PieceType.Rok    => new RookMoveValidator
    case engine.PieceType.Bishop => new BishopValidator
  }
}

class RookMoveValidator extends MoveValidator {

  override def validate(source: Square, destination: Square, board: Board): Either[MoveError, Unit] = {
    val destinationBoardSquareOccupied = board.getPieceAt(destination).nonEmpty

    if (destinationBoardSquareOccupied) {
      Left(DestinationSquareOccupied)
    } else {
      if (source.row == destination.row) {
        val pieces = board.getPiecesHorizontally(source.row, source.column, destination.column)
        if (pieces.isEmpty)
          Right(())
        else
          Left(IllegalMove)
      } else if (
        source.column == destination.column &&
        board.getPiecesVertically(source.column, source.row, destination.row).isEmpty
      ) {
        Right(())
      } else Left(IllegalMove)
    }
  }
}

class BishopValidator extends MoveValidator {

  override def validate(source: Square, destination: Square, board: Board): Either[MoveError, Unit] = {
    val destinationBoardSquareOccupied = board.getPieceAt(destination).nonEmpty
    lazy val diagonals = List(
      Diagonal.topLeft(source),
      Diagonal.topRight(source),
      Diagonal.downLeft(source),
      Diagonal.downRight(source)
    )
    lazy val foundDiagonal = diagonals.find(diagonalSquares => diagonalSquares.contains(destination))
    lazy val freePath = foundDiagonal.fold(false) { squares =>
      val indexOfDestination      = squares.indexOf(destination)
      val squaresUntilDestination = squares.slice(0, indexOfDestination)
      squaresUntilDestination.forall(board.isEmpty)
    }

    if (destinationBoardSquareOccupied) {
      Left(DestinationSquareOccupied)
    } else if (freePath) {
      Right(())
    } else
      Left(IllegalMove)
  }
}
