ThisBuild / organization := "com.example"
ThisBuild / homepage := Some(url("https://github.com/sbt/sbt-hello"))

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-hello",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.2.8" // set minimum sbt version
      }
    }
  )

val artifactory = "https://itvrepos.jfrog.io/itvrepos/user-libs"
ThisBuild / publishTo := {
  if (isSnapshot.value)
    Some("Artifactory Realm" at artifactory)
  else
    Some("Artifactory Realm" at artifactory + ";build.timestamp=" + new java.util.Date().getTime)
}
