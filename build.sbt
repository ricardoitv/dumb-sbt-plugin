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
    ,libraryDependencies += "org.reflections" % "reflections" % "0.10.2"
  )

val artifactory = "https://itvrepos.jfrog.io/itvrepos/oasvc-ivy"
ThisBuild / publishTo := {
  if (isSnapshot.value)
    Some("Artifactory Realm" at artifactory)
  else
    Some("Artifactory Realm" at artifactory + ";build.timestamp=" + new java.util.Date().getTime)
}

lazy val artifactoryCredentials = {
  val maybeCredentials = for {
    host <- sys.env.get("ARTIFACTORY_HOST")
    user <- sys.env.get("ARTIFACTORY_USER")
    pass <- sys.env.get("ARTIFACTORY_PASSWORD")
  } yield Credentials("Artifactory Realm", host, user, pass)
  maybeCredentials.getOrElse(Credentials(Path.userHome / ".ivy2" / ".credentials"))
}
ThisBuild / credentials += artifactoryCredentials
