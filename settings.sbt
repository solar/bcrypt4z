organization := "org.sazabi"

scalaVersion := "2.11.7"

crossScalaVersions := Seq(scalaVersion.value, "2.10.5")

incOptions := incOptions.value.withNameHashing(true)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:implicitConversions")

libraryDependencies += "com.github.scalaprops" %% "scalaprops" % "0.2.1" % "test"

testFrameworks += new TestFramework("scalaprops.ScalapropsFramework")
parallelExecution in Global := false

releasePublishArtifactsAction := PgpKeys.publishSigned.value
releaseCrossBuild := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>https://github.com/solar/bcrypt4z</url>
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt"</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:solar/bcrypt4z.git</url>
    <connection>scm:git:git@github.com:solar/bcrypt4z.git</connection>
  </scm>
  <developers>
    <developer>
      <id>solar</id>
      <name>Shinpei Okamura</name>
      <url>https://github.com/solar</url>
    </developer>
  </developers>)
