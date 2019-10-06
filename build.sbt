import Version._

val V = new {
  val distage = "0.9.5"
  val scalatest = "3.0.8"
  val scalacheck = "1.14.2"
  val http4s = "0.21.0-M4"
  val zio = "1.0.0-RC12-1"
  val zioCats = "2.0.0.0-RC3"
  val kindProjector = "0.11.0"
  val circeDerivation = "0.12.0-M7"
  val cats = "2.0.0"
  val circe2_12 = "0.11.1"
  val circe: Version = {
    case Some((2, 13)) => "0.12.1"
    case _ => circe2_12
  }
  val circeExtras: Version = {
    case Some((2, 13)) => "0.12.2"
    case _ => circe2_12
  }
  val tapir = "0.9.1"
  val sttp = "1.6.8"
  val refined = "0.9.10"
  val commonsText = "1.8"
  val chimney = "0.3.2"
  val pureconfig = "0.12.1"
  val zioDelegate = "0.0.3"
  val scalaCsv = "1.3.6"
  val betterMonadicFor = "0.3.1"
}

val Deps = new {
  val scalatest = "org.scalatest" %% "scalatest" % V.scalatest
  val scalacheck = "org.scalacheck" %% "scalacheck" % V.scalacheck

  val distageCore = "io.7mind.izumi" %% "distage-core" % V.distage
  val distageRoles = "io.7mind.izumi" %% "distage-roles" % V.distage
  val distageStatic = "io.7mind.izumi" %% "distage-static" % V.distage
  val distageConfig = "io.7mind.izumi" %% "distage-config" % V.distage
  val distageTestkit = "io.7mind.izumi" %% "distage-testkit" % V.distage
  val logstageCore = "io.7mind.izumi" %% "logstage-core" % V.distage

  val http4sDsl = "org.http4s" %% "http4s-dsl" % V.http4s
  val http4sServer = "org.http4s" %% "http4s-blaze-server" % V.http4s
  val http4sClient = "org.http4s" %% "http4s-blaze-client" % V.http4s
  val http4sCirce = "org.http4s" %% "http4s-circe" % V.http4s

  val circeDerivation = "io.circe" %% "circe-derivation" % V.circeDerivation

  val kindProjector = "org.typelevel" %% "kind-projector" % V.kindProjector cross CrossVersion.full
  val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % V.betterMonadicFor

  val zio = "dev.zio" %% "zio" % V.zio
  val zioTestSbt = "dev.zio" %% "zio-test-sbt" % V.zio
  val zioDelegate = "dev.zio" %% "zio-delegate" % V.zioDelegate
  val zioCats = "dev.zio" %% "zio-interop-cats" % V.zioCats

  val tapirJsonCirce = "com.softwaremill.tapir" %% "tapir-json-circe" % V.tapir
  val tapirSttpClient = "com.softwaremill.tapir" %% "tapir-sttp-client" % V.tapir

  val asyncHttpClientBackendZio = "com.softwaremill.sttp" %% "async-http-client-backend-zio" % V.sttp
}

val scala2_12 = "2.12.10"
val scala2_13 = "2.13.1"

def dependenciesFor(version: String)(deps: (Option[(Long, Long)] => ModuleID)*): Seq[ModuleID] =
  deps.map(_.apply(CrossVersion.partialVersion(version)))

lazy val nonTestScalacOptions = "-Xfatal-warnings"

lazy val dummy = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    inThisBuild(Seq(
      scalaVersion := scala2_13,
      organization := "io.github.DmytroOrlov",
      addCompilerPlugin(Deps.kindProjector),
      addCompilerPlugin(Deps.betterMonadicFor),
      scalacOptions ++= nonTestScalacOptions :: List("-Ymacro-annotations", "-language:higherKinds"),
      scalacOptions in Test -= nonTestScalacOptions,
    )),
    libraryDependencies ++= dependenciesFor(scalaVersion.value)(
      "io.circe" %% "circe-core" % V.circe(_),
      "io.circe" %% "circe-generic" % V.circe(_),
      "io.circe" %% "circe-parser" % V.circe(_),
      "io.circe" %% "circe-refined" % V.circe(_),
      "io.circe" %% "circe-generic-extras" % V.circeExtras(_),
    ) ++ Seq(
      Deps.distageCore,
      Deps.distageRoles,
      Deps.distageStatic,
      Deps.distageConfig,
      Deps.distageTestkit % Test,
      Deps.scalatest % Test,
      Deps.scalacheck % Test,
      Deps.http4sDsl,
      Deps.http4sServer,
      Deps.http4sClient % Test,
      Deps.circeDerivation,
      Deps.zio,
      Deps.zioCats,
      Deps.zioTestSbt % Test,
      Deps.zioDelegate,

      "org.typelevel" %% "cats-core" % V.cats,
      "org.typelevel" %% "cats-effect" % V.cats,

      Deps.tapirJsonCirce,
      Deps.tapirSttpClient % Test,
      Deps.asyncHttpClientBackendZio % Test,
      "com.softwaremill.tapir" %% "tapir-http4s-server" % V.tapir,
      "com.softwaremill.tapir" %% "tapir-openapi-circe-yaml" % V.tapir,
      "com.softwaremill.tapir" %% "tapir-openapi-docs" % V.tapir,
      "com.softwaremill.tapir" %% "tapir-swagger-ui-http4s" % V.tapir,

      Deps.logstageCore,
      "io.7mind.izumi" %% "logstage-rendering-circe" % V.distage,
      // Router from Slf4j to LogStage
      "io.7mind.izumi" %% "logstage-adapter-slf4j" % V.distage,
      // Configure LogStage with Typesafe Config
      "io.7mind.izumi" %% "logstage-config" % V.distage,
      // LogStage integration with DIStage
      "io.7mind.izumi" %% "logstage-di" % V.distage,
      // Router from LogStage to Slf4J
      "io.7mind.izumi" %% "logstage-sink-slf4j" % V.distage,

      "org.apache.commons" % "commons-text" % V.commonsText,
      "eu.timepit" %% "refined" % V.refined,
      "io.scalaland" %% "chimney" % V.chimney,
      "com.github.pureconfig" %% "pureconfig" % V.pureconfig,

      "com.github.tototoshi" %% "scala-csv" % V.scalaCsv % Test,
    ),
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))
