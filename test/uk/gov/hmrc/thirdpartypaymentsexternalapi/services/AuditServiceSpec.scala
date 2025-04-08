package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import play.api.libs.json._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit.{InitiateJourneyAuditDetail, Outcome}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec

import java.util.UUID

class AuditServiceSpec extends ItSpec {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private val auditConnector = app.injector.instanceOf[AuditConnector]
  val auditService: AuditService = app.injector.instanceOf[AuditService]

  "AuditService" - {

    "should successfully send audit when the journey is successful" in {
      val isSuccessful = true
      val maybeErrors: Option[Seq[String]] = None
      val rawJson: Option[JsValue] = Some(Json.obj(
        "reference" -> "ref123",
        "amountInPence" -> 1000,
        "friendlyName" -> "Test Origin",
        "taxRegime" -> "SomeTaxRegime"
      ))
      val maybeClientJourneyId: Option[ClientJourneyId] = Some(ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96")))

      auditService.auditInitiateJourneyResult(isSuccessful, maybeErrors, rawJson, maybeClientJourneyId)

      auditConnector.sendExplicitAudit("Initiate Journey", InitiateJourneyAuditDetail(
        outcome = Outcome(isSuccessful, maybeErrors),
        taxRegime = Some("SomeTaxRegime"),
        paymentReference = Some("ref123"),
        amount = Some(10.0),
        originOfRequest = Some("Test Origin"),
        clientJourneyId = Some("clientJourneyId123")
      ))

    }

  }

}
