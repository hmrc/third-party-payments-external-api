package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, TaxRegime}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit.{InitiateJourneyAuditDetail, Outcome}
import java.util.UUID

class AuditService {

  private def toInitiateJourneyResult(
                                       outcome: Outcome,
                                       taxRegime: TaxRegime,
                                       paymentReference: String,
                                       amount: Int,
                                       originOfRequest: String,
                                       clientJourneyId: ClientJourneyId
                                     )(using HeaderCarrier): Unit =
    InitiateJourneyAuditDetail(
      outcome = Outcome(true),
      taxRegime = String,
      paymentReference = String,
      amount = 1,
      originOfRequest = String,
      clientJourneyId = ClientJourneyId(UUID)
    )

}
