/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.config

import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.Configuration

class AppConfigSpec extends AnyWordSpecLike {

  "externalTestEnabled" should {
    "return false when the config value is not set" in {
      new AppConfig(Configuration()).externalTestEnabled mustBe false
    }

    "return true when the config value is set to true" in {
      new AppConfig(Configuration("external-test.enabled" -> true)).externalTestEnabled mustBe true
    }

    "return false when the config value is set to false" in {
      new AppConfig(Configuration("external-test.enabled" -> false)).externalTestEnabled mustBe false
    }
  }
}
