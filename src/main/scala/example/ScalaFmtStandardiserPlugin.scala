package example

import sbt.*
import sbt.Keys.*
import java.nio.charset.Charset
import java.nio.file.{Files, Paths, StandardOpenOption}

object ScalaFmtStandardiserPlugin extends AutoPlugin {
  // this enables the plugin automatically so we don't need to use `.enablePlugins(ScalaFmtStandardiserPlugin)` on the project
  override def trigger = allRequirements

  object autoImport {
    val commitCheck = taskKey[Unit]("commitCheck")
    val cc = taskKey[Unit]("alias for commitCheck")
  }

  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    autoImport.commitCheck := {
      // TODO: these are all the commands perso data loader has on the commitCheck alias:
      //  """clean; eval scala.util.Properties.setProp("CI", ""); project service; compile; dependencyUpdates; scalafmtAll; test; IntegrationTest/test"""
      Def
        .sequential(
          Compile / clean,
          Compile / compile,
          Test / test,
          //IntegrationTest / test // TODO: Enable this one only on projects that have Integration Tests otherwise this task fails
        )
        .value
    },
    autoImport.cc := {
      autoImport.commitCheck.value
    }
  )

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
