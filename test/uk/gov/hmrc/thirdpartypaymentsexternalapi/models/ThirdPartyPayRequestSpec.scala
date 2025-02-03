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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.{CorporationTax, EmployersPayAsYouEarn, SelfAssessment, Vat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartyPayRequest
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

import java.time.LocalDate
import java.util.UUID

class ThirdPartyPayRequestSpec extends UnitSpec {

  "ThirdPartyPayRequest" - {

    val testClientJourneyId: ClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))

      def thirdPartyPayRequest(taxRegime: TaxRegime): ThirdPartyPayRequest = ThirdPartyPayRequest(
        taxRegime     = taxRegime,
        reference     = "someReference",
        amountInPence = 123,
        friendlyName  = Some("Test Company"),
        backURL       = "some-back-url",
        dueDate       = Some(LocalDate.of(2025, 1, 31))
      )

      def jsValue(taxRegimeString: String) = Json.parse(s"""{"taxRegime":"$taxRegimeString","reference":"someReference","amountInPence":123,"friendlyName":"Test Company","backURL":"some-back-url","dueDate":"2025-01-31"}""")

    "serialise to json" in {
      Json.toJson(thirdPartyPayRequest(SelfAssessment)) shouldBe jsValue("SelfAssessment")
    }

    "de serialise from json" in {
      Json.fromJson[ThirdPartyPayRequest](jsValue("SelfAssessment")).asEither shouldBe Right(thirdPartyPayRequest(SelfAssessment))
    }

    "asSaSpjRequest correctly creates SpjRequest3psSa" in {
      val spjRequest = SpjRequest3psSa("someReference", 123, testClientJourneyId, Some("Test Company"), Some("some-back-url"), Some("some-back-url"))
      thirdPartyPayRequest(SelfAssessment).asSaSpjRequest(testClientJourneyId) shouldBe spjRequest
    }

    "asVatSpjRequest correctly creates SpjRequest3psVat" in {
      val spjRequest = SpjRequest3psVat("someReference", 123, testClientJourneyId, Some("Test Company"), Some("some-back-url"), Some("some-back-url"))
      thirdPartyPayRequest(Vat).asVatSpjRequest(testClientJourneyId) shouldBe spjRequest
    }

    "asCorporationTaxSpjRequest correctly creates SpjRequest3psCorporationTax" in {
      val spjRequest = SpjRequest3psCorporationTax("someReference", 123, testClientJourneyId, Some("some-back-url"), Some("some-back-url"))
      thirdPartyPayRequest(CorporationTax).asCorporationTaxSpjRequest(testClientJourneyId) shouldBe spjRequest
    }

    "asEmployersPayAsYouEarnSpjRequest correctly creates SpjRequest3psEmployersPayAsYouEarn" in {
      val spjRequest = SpjRequest3psEmployersPayAsYouEarn("someReference", 123, testClientJourneyId, Some("some-back-url"), Some("some-back-url"))
      thirdPartyPayRequest(EmployersPayAsYouEarn).asEmployersPayAsYouEarnSpjRequest(testClientJourneyId) shouldBe spjRequest
    }
  }
}
