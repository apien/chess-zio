import com.github.apien.chesszio.api.ChessRoutes
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{Task, ZLayer}

class Endpoints(chessEndpoints: ChessRoutes) {

  val endpoints: List[ZServerEndpoint[Any, Any]] = {
    val api  = chessEndpoints.routes
    val docs = docsEndpoints(api)
    api ++ docs
  }

  private def docsEndpoints(apiEndpoints: List[ZServerEndpoint[Any, Any]]): List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter()
      .fromServerEndpoints[Task](apiEndpoints, "chess-zio", "0.1.0")
}

object Endpoints {
  val live = ZLayer.fromFunction(Endpoints(_))
}
