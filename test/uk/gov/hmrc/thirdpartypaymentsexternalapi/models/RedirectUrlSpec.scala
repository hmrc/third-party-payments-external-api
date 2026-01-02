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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.NextUrl
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

class RedirectUrlSpec extends UnitSpec {

  "NextUrl" - {
    "serialise to json" in {
      Json.toJson(NextUrl("https://www.some-url.com")) shouldBe JsString("https://www.some-url.com")
    }

    "de serialise from json" in {
      Json.fromJson[NextUrl](JsString("https://www.some-url.com")).asEither shouldBe Right(
        NextUrl("https://www.some-url.com")
      )
    }
  }

}
