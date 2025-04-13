package com.github.apien.chesszio.engine.move

import com.github.apien.chesszio.engine.PieceType.Rook
import com.github.apien.chesszio.engine.move.BishopMoveValidatorSpec.suite
import com.github.apien.chesszio.engine.move.MoveError.IllegalMove
import com.github.apien.chesszio.engine.move.RookMoveValidatorSpec.test
import com.github.apien.chesszio.engine.move.{BishopValidator, MoveError}
import com.github.apien.chesszio.engine.{Board, Piece, Square}
import com.github.apien.chesszio.test.TestDataBuilder
import zio.test.*

object BishopMoveValidatorSpec extends ZIOSpecDefault with TestDataBuilder {
  private val board = new Board(
    Map(
      unsafeSquare(3, 3) -> Piece("P1", Rook, false),
      unsafeSquare(5, 1) -> Piece("P1", Rook, false),
      unsafeSquare(1, 1) -> Piece("P1", Rook, false),
      unsafeSquare(6, 6) -> Piece("P1", Rook, false),
      unsafeSquare(1, 5) -> Piece("P1", Rook, false)
    )
  )

  private val validator = new BishopValidator

  def spec = suite("BishopValidator")(
    validateSpec
  )

  def validateSpec = suite("validate")(
    test(
      "return DestinationSquareOccupied when a destination field is not empty"
    ) {
      val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(5, 1), board)
      assertTrue(result == Left(MoveError.DestinationSquareOccupied))
    },
    test(
      "return IllegalMove at attempt to move horizontally"
    ) {
      val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(6, 3), board)
      assertTrue(result == Left(MoveError.IllegalMove))
    },
    test(
      "return IllegalMove at attempt to move vertically"
    ) {
      val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(2, 3), board)
      assertTrue(result == Left(MoveError.IllegalMove))
    },
    suite("move on top left diagonal")(
      test("allow for a move when a path is free") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(2, 2), board)
        assertTrue(result == Right(()))
      },
      test("return IllegalMove when another piece is on a path") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(0, 0), board)
        assertTrue(result == Left((IllegalMove)))
      }
    ),
    suite("move on top right diagonal")(
      test("allow for a move when a path is free") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(4, 2), board)
        assertTrue(result == Right(()))
      },
      test("return IllegalMove when another piece is on a path") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(6, 0), board)
        assertTrue(result == Left((IllegalMove)))
      }
    ),
    suite("move on down left diagonal")(
      test("allow for a move when a path is free") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(2, 4), board)
        assertTrue(result == Right(()))
      },
      test("return IllegalMove when another piece is on a path") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(0, 6), board)
        assertTrue(result == Left((IllegalMove)))
      }
    ),
    suite("move on down right diagonal")(
      test("allow for a move when a path is free") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(5, 5), board)
        assertTrue(result == Right(()))
      },
      test("return IllegalMove when another piece is on a path") {
        val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(7, 7), board)
        assertTrue(result == Left((IllegalMove)))
      }
    )
  )

}
