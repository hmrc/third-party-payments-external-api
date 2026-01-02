import sbt.Keys.*
import play.sbt.routes.RoutesKeys.routes
import sbt.*
import wartremover.Wart
import wartremover.WartRemover.autoImport.*

object WartRemoverSettings {

  val wartRemoverSettings =
    Seq(
      (Compile / compile / wartremoverErrors) ++= {
        if (StrictBuilding.strictBuilding.value) {
          Warts.allBut(
            Wart.Equals,
            Wart.DefaultArguments,
            Wart.ImplicitParameter,
            Wart.Nothing,
            Wart.StringPlusAny,
            Wart.Throw,
            Wart.ToString,
            Wart.PlatformDefault
          )
        } else Nil
      },
      Test / compile / wartremoverErrors --= Seq(
        Wart.Any,
        Wart.Equals,
        Wart.GlobalExecutionContext,
        Wart.NonUnitStatements,
        Wart.PublicInference
      ),
      wartremoverExcluded ++= (Compile / routes).value
    )
}
