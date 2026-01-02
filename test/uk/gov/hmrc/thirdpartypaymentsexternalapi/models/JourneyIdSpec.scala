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

import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.JourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

class JourneyIdSpec extends UnitSpec {

  "JourneyId" - {
    "serialise to json" in {
      Json.toJson(JourneyId("aef0f31b-3c0f-454b-9d1f-07d549987a96")) shouldBe JsString(
        "aef0f31b-3c0f-454b-9d1f-07d549987a96"
      )
    }

    "de serialise from json" in {
      Json.fromJson[JourneyId](JsString("aef0f31b-3c0f-454b-9d1f-07d549987a96")).asEither shouldBe Right(
        JourneyId("aef0f31b-3c0f-454b-9d1f-07d549987a96")
      )
    }
  }

}
