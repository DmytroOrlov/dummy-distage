package dummy

import buildinfo.BuildInfo.version
import dummy.zio_env.{Endpoints, HealthCheckResponse}
import izumi.distage.model.definition.DIResource
import izumi.distage.model.definition.DIResource.DIResourceBase
import izumi.distage.roles.model.{RoleDescriptor, RoleService}
import izumi.functional.bio.BIO._
import izumi.functional.bio.BIOError
import izumi.fundamentals.platform.cli.model.raw.RawEntrypointParams
import org.http4s._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import zio._

trait HttpApi[F[_, _]] {
  def http: HttpRoutes[F[Throwable, ?]]
}

object HttpApi {

  final class Impl[F[+ _, + _] : Sync2 : ContextShift2 : BIOError] extends HttpApi[F] {
    override def http: HttpRoutes[F[Throwable, ?]] = {
      import tapir.server.http4s._
      val logic: Any => F[Unit, HealthCheckResponse] = { _: Any => F.pure(HealthCheckResponse(version)) }
      Endpoints.healthCheck.toRoutes(i => logic(i).attempt)
    }
  }

}

final class DummyRole(
    httpApi: HttpApi[IO],
)(implicit
    concurrentEffect2: ConcurrentEffect2[IO],
    timer2: Timer2[IO],
) extends RoleService[Task] {
  override def start(roleParameters: RawEntrypointParams, freeArgs: Vector[String]): DIResourceBase[Task, Unit] = {
    DIResource.fromCats {
      BlazeServerBuilder[Task]
        .withHttpApp(httpApi.http.orNotFound)
        .bindHttp(host = "0.0.0.0")
        .resource
    }.void
  }
}

object DummyRole extends RoleDescriptor {
  val id = "dummy"
}
