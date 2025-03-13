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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

import java.util.UUID

class SpjRequestSpec extends UnitSpec {

  "SpjRequest" - {

    val testClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
    val testReference = Reference("1234567895")
    val testAmountInPence = AmountInPence(123)
    val testFriendlyName = FriendlyName("Test Company")
    val testUrl = URL("https://valid-url.com")

    "SpjRequest3psSa" - {

      val spjRequest = SpjRequest3psSa(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), Some(testUrl), Some(testUrl))
      val jsValue = Json.parse("""{"utr":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")

      "serialise to json with optional due date" in {
        Json.toJson(spjRequest) shouldBe Json.parse("""{"utr":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")
      }

      "serialise to json" in {
        Json.toJson(spjRequest) shouldBe Json.parse("""{"utr":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")
      }

      "de serialise from json" in {
        Json.fromJson[SpjRequest3psSa](jsValue).asEither shouldBe Right(spjRequest)
      }
    }

    "SpjRequest3psVat" - {

      val spjRequest = SpjRequest3psVat(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), Some(testUrl), Some(testUrl))
      val jsValue = Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")

      "serialise to json with optional due date" in {
        Json.toJson(spjRequest) shouldBe Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")
      }
      "serialise to json" in {
        Json.toJson(spjRequest) shouldBe Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")
      }

      "de serialise from json with optional due date" in {
        Json.fromJson[SpjRequest3psVat](jsValue).asEither shouldBe Right(spjRequest)
      }
    }

    "SpjRequest3psCorporationTax" - {

      val spjRequest = SpjRequest3psCorporationTax(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), Some(testUrl), Some(testUrl))
      val jsValue = Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")

      "serialise to json" in {
        Json.toJson(spjRequest) shouldBe Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")
      }

      "de serialise from json" in {
        Json.fromJson[SpjRequest3psCorporationTax](jsValue).asEither shouldBe Right(spjRequest)
      }
    }

    "SpjRequest3psEmployersPayAsYouEarn" - {

      val spjRequest = SpjRequest3psEmployersPayAsYouEarn(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), Some(testUrl), Some(testUrl))
      val jsValue = Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")

      "serialise to json" in {
        Json.toJson(spjRequest) shouldBe Json.parse("""{"vrn":"1234567895","amountInPence":123,"clientJourneyId":"aef0f31b-3c0f-454b-9d1f-07d549987a96","friendlyName":"Test Company","returnUrl":"https://valid-url.com","backUrl":"https://valid-url.com"}""")
      }

      "de serialise from json" in {
        Json.fromJson[SpjRequest3psEmployersPayAsYouEarn](jsValue).asEither shouldBe Right(spjRequest)
      }
    }
  }
}
