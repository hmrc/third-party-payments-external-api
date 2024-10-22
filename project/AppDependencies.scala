import sbt.*

object AppDependencies {

  private val bootstrapVersion = "9.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-30" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test,

  )

  val it: Seq[ModuleID] = Seq.empty[ModuleID]
}