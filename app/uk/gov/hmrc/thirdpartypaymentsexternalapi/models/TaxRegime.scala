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

import enumeratum.{PlayJsonEnum, Enum, EnumEntry}

sealed trait TaxRegime extends EnumEntry

object TaxRegime extends Enum[TaxRegime] with PlayJsonEnum[TaxRegime] {

  val values: IndexedSeq[TaxRegime] = findValues

  case object SelfAssessment extends TaxRegime
  case object Vat extends TaxRegime
  case object CorporationTax extends TaxRegime
  case object EmployersPayAsYouEarn extends TaxRegime

}
