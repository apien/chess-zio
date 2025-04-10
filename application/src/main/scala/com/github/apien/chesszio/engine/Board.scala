package com.github.apien.chesszio.engine

class Board(private val squares: Map[Square, Piece]) {

  def getBoardState: Map[Square, Piece]                      = squares
  
  def findPieceById(pieceId: PieceId): Option[(Square, Piece)] = squares.find(_._2.id == pieceId)

  def getPieceAt(square: Square): Option[Piece] = squares.get(square)

  def isEmpty(square: Square): Boolean = getPieceAt(square).isEmpty

  def move(source: Square, destination: Square): Board = {
    squares.get(source).fold(this) { piece =>
      val updatedSquares = squares.removed(source) ++ Map(destination -> piece)
      new Board(updatedSquares)
    }
  }

  /** @param beginning
    *   Exclusive.
    * @param end
    *   Inclusive.
    * @return
    */
  def getPiecesHorizontally(row: Row, beginning: Column, end: Column): List[(Square, Piece)] = {
    if (beginning < end)
      squares
        .filter { case (square, _) => square.row == row }
        .filter { case (square, _) =>
          val result = square.column > beginning && square.column <= end
          result
        }
        .toList
    else {
      squares.view.filter { case (square, _) =>
        square.row == row && square.column < beginning && square.column >= end
      }.toList
    }
  }

  /** @param beginning
    *   Exclusive.
    * @param end
    *   Inclusive.
    * @return
    */
  def getPiecesVertically(column: Column, beginning: Row, end: Row): List[(Square, Piece)] = {
    if (beginning < end)
      squares.view.filter { case (square, _) =>
        square.column == column && square.row > beginning && square.row <= end
      }.toList
    else {
      squares.view.filter { case (square, _) =>
        square.column == column && square.row < beginning && square.row >= end
      }.toList
    }
  }
}
