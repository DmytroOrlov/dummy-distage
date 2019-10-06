import capture.Capture
import conf.HasAppConf.Service
import zio.ZIO

package object conf extends Service[HasAppConf] {
  val appEnv: ZIO[HasAppConf, Capture[AppError], AppEnv] =
    ZIO.accessM(_.appConfig.appEnv)
}
