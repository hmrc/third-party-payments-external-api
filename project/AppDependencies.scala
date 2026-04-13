import sbt.*

//format: OFF
object AppDependencies {

  private val bootstrapVersion = "10.7.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"   %% "bootstrap-backend-play-30" % bootstrapVersion,
    "com.beachape"  %% "enumeratum-play-json"      % "1.9.7",
    "org.typelevel" %% "cats-effect"               % "3.7.0",
    "uk.gov.hmrc"   %% "reference-checker"         % "2.7.0" cross CrossVersion.for3Use2_13
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapVersion % Test
  )

  val it: Seq[ModuleID] = Seq.empty[ModuleID]
}
