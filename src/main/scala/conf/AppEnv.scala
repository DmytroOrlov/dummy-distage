package conf

import capture.Capture
import conf.AppError.configErr
import conf.HasAppConf.Service
import pureconfig.ConfigReader
import pureconfig.ConfigSource.default.load
import pureconfig.generic.auto._
import pureconfig.generic.semiauto.deriveEnumerationReader
import zio.ZIO

case class ApplicationConf(appEnv: AppEnv)

sealed trait AppEnv

case object Prod extends AppEnv

case object Test extends AppEnv

object AppEnv {
  implicit val AppEnvHit: ConfigReader[AppEnv] = deriveEnumerationReader
}

trait HasAppConf {
  def appConfig: Service[Any]
}

object HasAppConf {

  trait Service[R] {
    def appEnv: ZIO[R, Capture[AppError], AppEnv]
  }

  trait Live extends HasAppConf {
    val appConfig = new Service[Any] {
      val appEnv = for {
        ApplicationConf(appEnv) <- ZIO.fromEither(load[ApplicationConf]).mapError(configErr)
      } yield appEnv
    }
  }

}
