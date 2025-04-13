package com.github.apien.chesszio

import com.github.apien.chesszio.MemoryPiecesRepository.{GamePieceKey, PieceSquareDb}
import com.github.apien.chesszio.engine.*
import com.github.apien.chesszio.engine.PieceType.Rook
import com.github.apien.chesszio.engine.move.MoveError
import com.github.apien.chesszio.test.TestDataBuilder
import zio.test.*
import zio.test.Assertion.{equalTo, fails}
import zio.{Ref, Scope, Task, ZIO, ZLayer}

object ChessServiceSpec extends ZIOSpecDefault with TestDataBuilder {

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("ChessServiceSpec")(
    moveSuite
  )

  private val gameId1: GameId   = "G1"
  private val gameId2: GameId   = "G2"
  private val pieceId1: PieceId = "P1"
  private val pieceId2: PieceId = "P2"
  private val pieceId3: PieceId = "P3"

  private def moveSuite = suite("move")(
    test("store a new state of board after a successful move") {
      for {
        service <- ZIO.service[ChessService]
        rookId  <- service.addPiece(gameId1, Rook, Square(Column.at0, Row.at0)).map(_.id)
        _       <- service.move(gameId1, rookId, Square(Column.at7, Row.at0))
        expectedBoard = Map(
          GamePieceKey(gameId1, rookId) -> PieceSquareDb(
            gameId1,
            rookId,
            Rook,
            deleted = false,
            Square(Column.at7, Row.at0)
          )
        )

        repository     <- ZIO.service[MemoryPiecesRepository]
        boardAfterMove <- repository.getAll
      } yield assertTrue(boardAfterMove == expectedBoard)
    }.provideShared(buildLayer()),
    test("do not modify state in case of invalid move") {
      for {
        service   <- ZIO.service[ChessService]
        rookId    <- service.addPiece(gameId1, Rook, Square(Column.at0, Row.at0)).map(_.id)
        moveError <- service.move(gameId1, rookId, Square(Column.at1, Row.at1)).exit
        expectedBoard = Map(
          GamePieceKey(gameId1, rookId) -> PieceSquareDb(
            gameId1,
            rookId,
            Rook,
            deleted = false,
            Square(Column.at0, Row.at0)
          )
        )

        repository     <- ZIO.service[MemoryPiecesRepository]
        boardAfterMove <- repository.getAll
      } yield assertTrue(boardAfterMove == expectedBoard) && assert(moveError)(fails(equalTo(MoveError.IllegalMove)))
    }.provideShared(buildLayer()),
    test("fail with PieceDoesNotExist when selected piece does not exist") {
      for {
        service <- ZIO.service[ChessService]
        result  <- service.move(gameId1, pieceId1, Square(Column.at0, Row.at0)).exit
      } yield assert(result)(fails(equalTo(MoveError.PieceDoesNotExist)))
    }.provideShared(buildLayer()),
    test("fail with PieceDoesNotExist when deleted piece has been removed") {
      for {
        service   <- ZIO.service[ChessService]
        rookId    <- service.addPiece(gameId1, Rook, Square(Column.at0, Row.at0)).map(_.id)
        _         <- service.removePiece(gameId1, rookId)
        moveError <- service.move(gameId1, rookId, Square(Column.at1, Row.at0)).exit
      } yield assert(moveError)(fails(equalTo(MoveError.PieceDoesNotExist)))
    }.provideShared(buildLayer())
  )

  private def buildLayer(
    initialPieces: Map[GamePieceKey, PieceSquareDb] = Map()
  ): ZLayer[Any, Nothing, ChessService & MemoryPiecesRepository] = {
    val repoLayer: ZLayer[Any, Nothing, MemoryPiecesRepository] =
      ZLayer.fromZIO(Ref.make(initialPieces)) >>> ZLayer.fromFunction(
        new MemoryPiecesRepository(_)
      )
    MemoryActionRepository.live >+> repoLayer >+> ChessService.live
  }

  class NoActionPublisher extends ActionPublisher {
    override def produce[A <: Action](action: A): Task[Unit] = ZIO.unit
  }

}
