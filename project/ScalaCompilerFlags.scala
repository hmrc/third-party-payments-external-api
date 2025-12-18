object ScalaCompilerFlags {

  val scalaCompilerOptions: Seq[String] = Seq(
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:strictEquality",
    "-Wconf:src=routes/.*:s"
  )

  val strictScalaCompilerOptions: Seq[String] = Seq(
    "-Xfatal-warnings",
    "-Wunused:implicits",
    "-Wunused:imports",
    "-Wunused:locals",
    "-Wunused:params",
    "-Wunused:privates",
    "-deprecation",
    "-feature",
    "-unchecked"
  )
}
