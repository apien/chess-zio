package com.github.apien.chesszio.test

import com.github.apien.chesszio.engine.{Column, Piece, PieceId, PieceType, Row, Square}

import java.util.UUID

trait TestDataBuilder {

  def unsafeSquare(column: Int, row: Int): Square = {
    // TODO some problems with refineUnsafe
    val columnParsed: Column = column match {
      case 0 => Column.at0
      case 1 => Column.at1
      case 2 => Column.at2
      case 3 => Column.at3
      case 4 => Column.at4
      case 5 => Column.at5
      case 6 => Column.at6
      case 7 => Column.at7
      case c => throw new RuntimeException(s"Unable to parse $c to column")
    }

    val rowParsed: Row = row match {
      case 0 => Row.at0
      case 1 => Row.at1
      case 2 => Row.at2
      case 3 => Row.at3
      case 4 => Row.at4
      case 5 => Row.at5
      case 6 => Row.at6
      case 7 => Row.at7
      case c => throw new RuntimeException(s"Unable to parse $c to column")
    }

    Square(columnParsed, rowParsed)
  }

  def buildPiece(
                  id: PieceId = UUID.randomUUID().toString,
                  kind: PieceType = PieceType.Rook,
                  deleted: Boolean = false
  ): Piece = Piece(id, kind, deleted)

}
