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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.connectors.PayApiConnector
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.SpjResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyPayRequest, ThirdPartyResponseError, ThirdPartyResponseErrors}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayApiService @Inject() (payApiConnector: PayApiConnector)(implicit executionContext: ExecutionContext) {

  def startPaymentJourney(thirdPartyPayRequest: ThirdPartyPayRequest)(implicit headerCarrier: HeaderCarrier): Future[Either[ThirdPartyResponseError, SpjResponse]] = {
    val spjResponseF: Future[SpjResponse] = thirdPartyPayRequest.taxRegime match {
      case TaxRegime.SelfAssessment        => payApiConnector.startSelfAssessmentJourney(thirdPartyPayRequest.asSaSpjRequest())
      case TaxRegime.Vat                   => payApiConnector.startVatJourney(thirdPartyPayRequest.asVatSpjRequest())
      case TaxRegime.CorporationTax        => payApiConnector.startCorporationTaxJourney(thirdPartyPayRequest.asCorporationTaxSpjRequest())
      case TaxRegime.EmployersPayAsYouEarn => payApiConnector.startEmployersPayAsYouEarnJourney(thirdPartyPayRequest.asEmployersPayAsYouEarnSpjRequest())
    }

    spjResponseF
      .map(Right(_))
      .recover {
        case _: UpstreamErrorResponse => Left(ThirdPartyResponseErrors.UpstreamError)
      }
  }

}
