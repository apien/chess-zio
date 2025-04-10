package com.github.apien.chesszio.engine

import com.github.apien.chesszio.engine.ChessEnginesSpec.test
import com.github.apien.chesszio.engine.move.MoveError
import com.github.apien.chesszio.test.TestDataBuilder
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

object ChessEnginesSpec extends ZIOSpecDefault with TestDataBuilder {

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("ChessEngine")(moveSuite)

  private def moveSuite = suite("move")(
    test("return PieceDoesNotExist when a piece with given id does not exist") {
      val engine = ChessEngine(Board(Map(unsafeSquare(0, 1) -> buildPiece())))

      val result = engine.move("notExist", unsafeSquare(3, 3))

      zio.test.assertTrue(result == Left(MoveError.PieceDoesNotExist))
    },
    test("return IllegalMove when a requested move is not possible for given piece") {
      val engine = ChessEngine(Board(Map(unsafeSquare(0, 1) -> buildPiece(id = "P1"))))

      val result = engine.move("P1", unsafeSquare(3, 3))

      zio.test.assertTrue(result == Left(MoveError.IllegalMove))
    },
    test("return DestinationSquareOccupied when a requested square is occupied - move is valid") {
      val engine = ChessEngine(
        Board(Map(unsafeSquare(0, 1) -> buildPiece(id = "P1"), unsafeSquare(6, 0) -> buildPiece()))
      )

      val result = engine.move("P1", unsafeSquare(6, 0))

      zio.test.assertTrue(result == Left(MoveError.DestinationSquareOccupied))
    },
    test("return DestinationSquareOccupied when a requested square is occupied - no free path") {
      val engine = ChessEngine(
        Board(
          Map(
            unsafeSquare(0, 1) -> buildPiece(id = "P1"),
            unsafeSquare(4, 0) -> buildPiece(),
            unsafeSquare(6, 0) -> buildPiece()
          )
        )
      )

      val result = engine.move("P1", unsafeSquare(6, 0))

      zio.test.assertTrue(result == Left(MoveError.DestinationSquareOccupied))
    },
    test("return updated object when a move is valid") {
      val engine = ChessEngine(
        Board(
          Map(
            unsafeSquare(0, 1) -> buildPiece(id = "P1"),
            unsafeSquare(7, 0) -> buildPiece(id = "P2")
          )
        )
      )
      val expectedStateAfterMove = Map(
        unsafeSquare(6, 1) -> buildPiece(id = "P1"),
        unsafeSquare(7, 0) -> buildPiece(id = "P2")
      )

      val resultSquares = engine.move("P1", unsafeSquare(6, 1)).map(_.getState)

      zio.test.assertTrue(resultSquares == Right(expectedStateAfterMove))
    }
  )
}
