package dummy

import izumi.distage.plugins.load.PluginLoader.PluginConfig
import izumi.distage.roles.{BootstrapConfig, RoleAppLauncher, RoleAppMain}
import izumi.fundamentals.platform.cli.model.raw.RawRoleParams
import zio.Task
import zio.interop.catz._

object Main extends RoleAppMain.Default[Task](
  launcher = new RoleAppLauncher.LauncherF[Task] {
    override val bootstrapConfig = BootstrapConfig(
      PluginConfig(
        debug = false,
        packagesEnabled = Seq("dummy.plugins"),
        packagesDisabled = Nil,
      )
    )
  }
) {
  override val requiredRoles = Vector(RawRoleParams.empty(DummyRole.id))
}
