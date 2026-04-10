/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.{JsPath, JsonValidationError}
import uk.gov.hmrc.referencechecker.{CorporationTaxReferenceChecker, EpayeReferenceChecker, SelfAssessmentReferenceChecker, VatReferenceChecker}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartyPayRequest

@Singleton
final class ReferenceValidationService @Inject() {

  def validateReference: ThirdPartyPayRequest => Either[collection.Seq[(JsPath, collection.Seq[JsonValidationError])], ThirdPartyPayRequest] = {
    (thirdPartyPayRequest: ThirdPartyPayRequest) =>
      val validationFunction: ThirdPartyPayRequest => Either[collection.Seq[
        (JsPath, collection.Seq[JsonValidationError])
      ], ThirdPartyPayRequest] = thirdPartyPayRequest.taxRegime match {
        case TaxRegime.SelfAssessment        => validateSelfAssessmentReference
        case TaxRegime.Vat                   => validateVatReference
        case TaxRegime.CorporationTax        => validateCorporationTaxReference
        case TaxRegime.EmployersPayAsYouEarn => validateEmployersPayAsYouEarnReference
      }
      validationFunction(thirdPartyPayRequest)
  }

  private def validateSelfAssessmentReference: ThirdPartyPayRequest => Either[collection.Seq[
    (JsPath, collection.Seq[JsonValidationError])
  ], ThirdPartyPayRequest] = request => {
    if (SelfAssessmentReferenceChecker.isValid(request.reference.value)) Right(request)
    else
      Left(
        Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.saReference.invalid")))))
      )
  }

  private def validateVatReference: ThirdPartyPayRequest => Either[collection.Seq[
    (JsPath, collection.Seq[JsonValidationError])
  ], ThirdPartyPayRequest] = request => {
    if (VatReferenceChecker.isValid(request.reference.value)) Right(request)
    else
      Left(
        Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.vatReference.invalid")))))
      )
  }

  private def validateCorporationTaxReference: ThirdPartyPayRequest => Either[collection.Seq[
    (JsPath, collection.Seq[JsonValidationError])
  ], ThirdPartyPayRequest] = request => {
    if (CorporationTaxReferenceChecker.isValid(request.reference.value)) Right(request)
    else
      Left(
        Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.ctReference.invalid")))))
      )
  }

  private def validateEmployersPayAsYouEarnReference: ThirdPartyPayRequest => Either[collection.Seq[
    (JsPath, collection.Seq[JsonValidationError])
  ], ThirdPartyPayRequest] = request => {
    if (EpayeReferenceChecker.isValid(request.reference.value)) Right(request)
    else
      Left(
        Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.epayeReference.invalid")))))
      )
  }
}
