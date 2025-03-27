package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit

import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId

final case class InitiateJourneyAuditDetail(
                              outcome: (String, Boolean),
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

}

//  details fields :=
//
//  "outcome" : {
//    "isSuccessful" : true
//  },
//  "taxRegime" : "SelfAssessment",
//  "paymentReference" : "utr",
//  "amount" : 12.34,
//  "originOfRequest" : "Quick Books",
//  "clientJourneyId" : "a218f71d-9bf2-438e-851c-71d50866c2e9"

