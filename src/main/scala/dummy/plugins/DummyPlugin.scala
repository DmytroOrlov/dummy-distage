package dummy.plugins

import distage.plugins.PluginDef
import distage.{TagK, TagKK}
import izumi.distage.model.definition.ModuleDef
import dummy.{HttpApi, DummyRole}
import zio.IO

object DummyPlugin extends PluginDef {
  make[DummyRole]
  include(modules.api[IO])

  object modules {
    def api[F[+ _, + _] : TagKK](implicit ev: TagK[F[Throwable, ?]]): ModuleDef = new ModuleDef {
      make[HttpApi[F]].from[HttpApi.Impl[F]]
    }
  }

}
