/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.thirdpartypaymentsexternalapi.models

import play.api.libs.json.Json
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit.{InitiateJourneyAuditDetail, Outcome}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

import java.util.UUID

class InitiateJourneyAuditDetailSpec extends UnitSpec {

  "InitiateJourneyAuditDetail" - {

    val testOutcome: Outcome = Outcome(true)
    val testTaxRegime: String = ""
    val testPaymentReference: String = ""
    val testAmount: Int = 1
    val testOriginOfRequest: String = ""
    val testClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))


    val initiateJourneyAuditDetail = InitiateJourneyAuditDetail(testOutcome, testTaxRegime, testPaymentReference, testAmount, testOriginOfRequest, testClientJourneyId)
    val jsValue = Json.parse("""{"outcome":{"isSuccessful":true},"taxRegime":"","paymentReference":"","amount":1,"originOfRequest":"","clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96"}""")

    "serialise to json" in {
      Json.toJson(initiateJourneyAuditDetail) shouldBe jsValue
    }

    "de serialise from json" in {
      Json.fromJson[InitiateJourneyAuditDetail](jsValue).asEither shouldBe Right(initiateJourneyAuditDetail)
    }

  }
}
