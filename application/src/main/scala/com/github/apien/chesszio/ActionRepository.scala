package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.{PieceDoesNotExist, SquareOccupied}
import com.github.apien.chesszio.MemoryActionRepository.ActionMemoryDto
import com.github.apien.chesszio.MemoryPiecesRepository.{GamePieceKey, PieceSquareDb}
import com.github.apien.chesszio.engine.*
import zio.{IO, Ref, UIO, ZLayer}

trait ActionRepository {

  def store(action: Action): UIO[Unit]

  def markAsPublished(actionId: ActionId): UIO[Unit]

  def getNotPublished(): UIO[List[Action]]
}

class MemoryActionRepository(ref: Ref[Map[ActionId, ActionMemoryDto]]) extends ActionRepository {
  override def store(action: Action): UIO[Unit] = ref.update(_ + (action.actionId -> ActionMemoryDto.create(action)))

  override def markAsPublished(actionId: ActionId): UIO[Unit] =
    ref.update(_.updatedWith(actionId)(_.map(actionDb => actionDb.copy(published = true))))

  override def getNotPublished(): UIO[List[Action]] =
    ref.get.map(_.view.filterNot(_._2.published).map(_._2.action).toList)
}

object MemoryActionRepository {

  case class ActionMemoryDto(action: Action, published: Boolean)

  object ActionMemoryDto {
    def create(action: Action): ActionMemoryDto = ActionMemoryDto(action, published = false)
  }

  val live: ZLayer[Any, Nothing, ActionRepository] =
    ZLayer.fromZIO(Ref.make(Map.empty[ActionId, ActionMemoryDto])) >>> ZLayer.fromFunction(
      MemoryActionRepository(_)
    )

}
