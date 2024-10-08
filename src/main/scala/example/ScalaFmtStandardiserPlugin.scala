package example

import sbt.Keys.*
import sbt.{Def, *}

import java.nio.charset.Charset
import java.nio.file.{Files, Paths, StandardOpenOption}

object ScalaFmtStandardiserPlugin extends AutoPlugin {
  // this enables the plugin automatically so we don't need to use `.enablePlugins(ScalaFmtStandardiserPlugin)` on the project
  override def trigger = allRequirements

  private val commandAliases: Seq[Def.Setting[State => State]] =
    addCommandAlias(
      "commitCheck",
      """clean; compile; dependencyUpdates; scalafmtAll; ciTestAll"""
    ) ++ addCommandAlias(
      "cc",
      "commitCheck"
    ) ++ addCommandAlias(
      "ciTestAll",
      "Test/test; IntegrationTest/test"
    )

  override def buildSettings: Seq[Setting[?]] = commandAliases

  override lazy val globalSettings: Seq[Setting[?]] = Seq(
    onLoad := { state =>
      refreshScalaFmtConf()
      state
    }
  )

  private def refreshScalaFmtConf(): Unit = {
    val contents = """version=3.7.15
                     |runner.dialect=scala3
                     |maxColumn=160
                     |preset=default
                     |align.preset=most
                     |assumeStandardLibraryStripMargin = false
                     |rewrite.scala3.removeEndMarkerMaxLines = 1
                     |align.stripMargin = true
                     |align.openParenDefnSite=false
                     |continuationIndent.defnSite=2
                     |newlines.neverInResultType=true
                     |rewrite.rules=[RedundantBraces,RedundantParens,SortModifiers]
                     |rewriteTokens = {
                     |  "⇒": "=>"
                     |  "→": "->"
                     |  "←": "<-"
                     |}
                     |project.excludePaths = ["glob:**/Dependency.scala", "glob:**.sbt", "glob:**/project/Plugin.scala"]
                     |""".stripMargin

    val scalafmtFile = Paths.get("./.scalafmt.conf")
    Files.deleteIfExists(scalafmtFile)
    Files.write(
      scalafmtFile,
      contents.getBytes(Charset.forName("utf-8")),
      StandardOpenOption.CREATE_NEW
    )
    ()
  }
}
