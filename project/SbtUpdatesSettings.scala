import com.timushev.sbt.updates.Compat.ModuleFilter
import com.timushev.sbt.updates.UpdatesKeys.dependencyUpdates
import com.timushev.sbt.updates.UpdatesPlugin.autoImport.{dependencyUpdatesFailBuild, dependencyUpdatesFilter, moduleFilterRemoveValue}
import sbt.Keys.*
import sbt.{Def, *}
import xsbti.compile.CompileAnalysis
object SbtUpdatesSettings {
  lazy val sbtUpdatesSettings: Seq[Def.Setting[_ >: Boolean with Task[CompileAnalysis] with ModuleFilter]] = Seq(
    dependencyUpdatesFailBuild := StrictBuilding.strictBuilding.value,
    (Compile / compile) := ((Compile / compile) dependsOn dependencyUpdates).value,
    dependencyUpdatesFilter -= moduleFilter("org.scala-lang"),
    dependencyUpdatesFilter -= moduleFilter("com.typesafe.play")
  )
}
