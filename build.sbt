import sys.process.Process

organization := "io.grhodes.sbt"

name := "sbt-api-builder"

version := Process("git describe --tags --dirty --always").lineStream_!.head.stripPrefix("v").trim.replace("-dirty", "-SNAPSHOT")

sbtPlugin := true

libraryDependencies ++= Seq(
  "io.circe" %% "circe-java8"  % "0.8.0",
  "io.circe" %% "circe-parser" % "0.8.0",
  "io.circe" %% "circe-yaml"   % "0.6.1"
)

publishMavenStyle := false
bintrayRepository := "sbt-plugins"
bintrayPackageLabels := Seq("sbt","plugin")
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scriptedBufferLog := false
scriptedLaunchOpts ++= Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
