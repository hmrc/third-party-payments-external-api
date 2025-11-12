import sbt.*

//format: OFF
object AppDependencies {

  private val bootstrapVersion = "10.4.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-30" % bootstrapVersion,
    "com.beachape"  %% "enumeratum-play-json"      % "1.9.0",
    "org.typelevel" %% "cats-effect"               % "3.6.3"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )

  val it: Seq[ModuleID] = Seq.empty[ModuleID]
}
