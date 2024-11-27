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

import org.scalatest.AppendedClues.convertToClueful
import org.scalatest.Assertion
import play.api.libs.json.{JsString, Json}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

class TaxRegimeSpec extends UnitSpec {

  "TaxRegime" - {
    "serialise to json" in {
      checkAllRegimesCovered
      Json.toJson(TaxRegime.SelfAssessment) shouldBe JsString("SelfAssessment")
      Json.toJson(TaxRegime.Vat) shouldBe JsString("Vat")
      Json.toJson(TaxRegime.CorporationTax) shouldBe JsString("CorporationTax")
      Json.toJson(TaxRegime.EmployersPayAsYouEarn) shouldBe JsString("EmployersPayAsYouEarn")
    }

    "de serialise from json" in {
      checkAllRegimesCovered
      Json.fromJson[TaxRegime](JsString("SelfAssessment")).asEither shouldBe Right(TaxRegime.SelfAssessment)
      Json.fromJson[TaxRegime](JsString("Vat")).asEither shouldBe Right(TaxRegime.Vat)
      Json.fromJson[TaxRegime](JsString("CorporationTax")).asEither shouldBe Right(TaxRegime.CorporationTax)
      Json.fromJson[TaxRegime](JsString("EmployersPayAsYouEarn")).asEither shouldBe Right(TaxRegime.EmployersPayAsYouEarn)
    }
  }

  private def checkAllRegimesCovered: Assertion = TaxRegime.values.size shouldBe 4 withClue "Has a tax regime been added but not had the tests updated?"

}
