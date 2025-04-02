package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId

final case class InitiateJourneyAuditDetail(
                              outcome: Outcome,
                              taxRegime: String,
                              paymentReference: String,
                              amount: Int,
                              originOfRequest: String,
                              clientJourneyId: ClientJourneyId
                            ) extends AuditDetail
{
  val auditType: String = "InitiateJourney"
}

object InitiateJourneyAuditDetail {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[InitiateJourneyAuditDetail] = Json.format[InitiateJourneyAuditDetail]

}

