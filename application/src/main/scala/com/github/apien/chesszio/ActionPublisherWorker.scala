package com.github.apien.chesszio

import zio.*
import zio.stream.ZStream

class ActionPublisherWorker(actionRepository: ActionRepository, actionPublisher: ActionPublisher) {

  def run(): ZStream[Any, Throwable, Unit] =
    ZStream
      .repeatZIO(processNotPublishedRecords)
      .schedule(Schedule.spaced(1.minute))

  private def processNotPublishedRecords: Task[Unit] = {
    def publishAction(action: Action): Task[Unit] = {
      for {
        _ <- actionPublisher.produce(action)
        _ <- actionRepository.markAsPublished(action.actionId)
      } yield ()
    }

    (for {
      _                   <- ZIO.logInfo("Process publishing action to kafka is running.")
      notPublishedActions <- actionRepository.getNotPublished()
      _                   <- ZIO.foreachDiscard(notPublishedActions)(publishAction)
      _                   <- ZIO.logInfo("Process publishing action to kafka is over.")
    } yield ())
      .foldZIO(error => ZIO.logError(s"Action publisher failed: ${error.getMessage}"), result => ZIO.succeed(result))
  }

}

object ActionPublisherWorker {
  val live: ZLayer[ActionRepository & ActionPublisher, Nothing, ActionPublisherWorker] =
    ZLayer.fromFunction(ActionPublisherWorker(_, _))
}
