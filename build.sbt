import AddSettings._

val f = file("../settings.sbt")

lazy val root = project.in(file(".")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, defaultSbtFiles
).settings(
  publishArtifact := false
).aggregate(core, scalaz)

lazy val core = project.in(file("core")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(f)
).settings(
  name := "bcrypt4z",
  libraryDependencies ++= Seq(
    "com.github.mpilquist" %% "simulacrum" % "0.3.0"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
)

lazy val scalaz = project.in(file("scalaz")).settingSets(
  autoPlugins, buildScalaFiles, userSettings, sbtFiles(f)
).settings(
  name := "bcrypt4z-scalaz",
  libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.3"
).dependsOn(core)
