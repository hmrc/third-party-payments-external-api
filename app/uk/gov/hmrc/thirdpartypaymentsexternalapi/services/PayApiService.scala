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

import play.api.Logger
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.connectors.PayApiConnector
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.SpjResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty._
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, TaxRegime}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayApiService @Inject() (
    payApiConnector:                 PayApiConnector,
    clientJourneyIdGeneratorService: ClientJourneyIdGeneratorService
)(implicit executionContext: ExecutionContext) {

  def startPaymentJourney(thirdPartyPayRequest: ThirdPartyPayRequest)(implicit headerCarrier: HeaderCarrier): Future[Either[ThirdPartyResponseError, ThirdPartyPayResponse]] = {

    val clientJourneyId: ClientJourneyId = clientJourneyIdGeneratorService.nextClientJourneyId()

    val spjResponseF: Future[SpjResponse] = thirdPartyPayRequest.taxRegime match {
      case TaxRegime.SelfAssessment        => payApiConnector.startSelfAssessmentJourney(thirdPartyPayRequest.asSaSpjRequest(clientJourneyId))
      case TaxRegime.Vat                   => payApiConnector.startVatJourney(thirdPartyPayRequest.asVatSpjRequest(clientJourneyId))
      case TaxRegime.CorporationTax        => payApiConnector.startCorporationTaxJourney(thirdPartyPayRequest.asCorporationTaxSpjRequest(clientJourneyId))
      case TaxRegime.EmployersPayAsYouEarn => payApiConnector.startEmployersPayAsYouEarnJourney(thirdPartyPayRequest.asEmployersPayAsYouEarnSpjRequest(clientJourneyId))
    }

    spjResponseF
      .map(spjResponse => Right(ThirdPartyPayResponse(clientJourneyId = clientJourneyId, redirectURL = RedirectUrl(spjResponse.nextUrl.value))))
      .recover {
        case _: UpstreamErrorResponse => Left(ThirdPartyResponseErrors.UpstreamError)
        case e =>
          Logger(this.getClass).error(s"Unexpected error occurred when trying to start spj: ${e.getMessage}")
          Left(ThirdPartyResponseErrors.UnexpectedError("error when trying to start a journey upstream."))
      }
  }

}
