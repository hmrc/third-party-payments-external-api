package uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata

import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId

import java.util.UUID

object TestData {
  val clientJourneyId: ClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
}
