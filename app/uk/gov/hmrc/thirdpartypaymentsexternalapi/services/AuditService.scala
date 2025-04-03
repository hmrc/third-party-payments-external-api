package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, TaxRegime}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit.{InitiateJourneyAuditDetail, Outcome}

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuditService @Inject()(auditConnector: AuditConnector)(implicit ec: ExecutionContext) {

  def auditInitiateJourneyResult(
                                       outcome: Outcome,
                                       taxRegime: TaxRegime,
                                       paymentReference: String,
                                       amount: Int,
                                       originOfRequest: String,
                                       clientJourneyId: ClientJourneyId
                                     )(implicit hc: HeaderCarrier): Unit = {
    val auditDetails =
      InitiateJourneyAuditDetail(
      outcome = Outcome(true),
      taxRegime = String,
      paymentReference = String,
      amount = amount,
      originOfRequest = String,
      clientJourneyId = ClientJourneyId(UUID)
    )
    auditConnector.sendExplicitAudit[InitiateJourneyAuditDetail]("initiatePayment", auditDetails)

  }

}
