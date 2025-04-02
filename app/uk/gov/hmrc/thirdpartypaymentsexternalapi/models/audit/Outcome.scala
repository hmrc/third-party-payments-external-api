package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit

import play.api.libs.json.{Json, OFormat}

final case class Outcome(isSuccessful: Boolean) extends AnyVal

object Outcome {
  implicit val format: OFormat[Outcome] = Json.format[Outcome]
}
