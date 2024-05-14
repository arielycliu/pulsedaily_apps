val root = project
  .in(file("."))
  .settings(
    scalaVersion := "3.3.3",
    libraryDependencies ++= Seq(
      "com.disneystreaming.smithy4s" %% "smithy4s-core" % smithy4sVersion.value,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.http4s" %% "http4s-ember-server" % "0.23.18",
    ),
    fork := true,
  )
  .enablePlugins(Smithy4sCodegenPlugin)