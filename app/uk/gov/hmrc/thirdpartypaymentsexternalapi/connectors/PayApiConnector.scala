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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.connectors

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest, SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat, SpjResponse}

import java.net.URL
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PayApiConnector @Inject() (
    httpClientV2:   HttpClientV2,
    servicesConfig: ServicesConfig
)(implicit executionContext: ExecutionContext) {

  private val payApiBaseUrl: String = servicesConfig.baseUrl("pay-api") + "/pay-api"

  private val startSelfAssessmentJourneyUrl: URL = url"$payApiBaseUrl/third-party-software/self-assessment/journey/start"

  def startSelfAssessmentJourney(spjRequest: SpjRequest3psSa)(implicit headerCarrier: HeaderCarrier): Future[SpjResponse] =
    startPaymentJourney[SpjRequest3psSa](startSelfAssessmentJourneyUrl, spjRequest)(SpjRequest3psSa.format)

  private val startVatJourneyUrl: URL = url"$payApiBaseUrl/third-party-software/vat/journey/start"

  def startVatJourney(spjRequest: SpjRequest3psVat)(implicit headerCarrier: HeaderCarrier): Future[SpjResponse] =
    startPaymentJourney[SpjRequest3psVat](startVatJourneyUrl, spjRequest)(SpjRequest3psVat.format)

  private val startCorporationTaxJourneyUrl: URL = url"$payApiBaseUrl/third-party-software/corporation-tax/journey/start"

  def startCorporationTaxJourney(spjRequest: SpjRequest3psCorporationTax)(implicit headerCarrier: HeaderCarrier): Future[SpjResponse] =
    startPaymentJourney[SpjRequest3psCorporationTax](startCorporationTaxJourneyUrl, spjRequest)(SpjRequest3psCorporationTax.format)

  private val startEmployersPayAsYouEarnJourneyUrl: URL = url"$payApiBaseUrl/third-party-software/employers-pay-as-you-earn/journey/start"

  def startEmployersPayAsYouEarnJourney(spjRequest: SpjRequest3psEmployersPayAsYouEarn)(implicit headerCarrier: HeaderCarrier): Future[SpjResponse] =
    startPaymentJourney[SpjRequest3psEmployersPayAsYouEarn](startEmployersPayAsYouEarnJourneyUrl, spjRequest)(SpjRequest3psEmployersPayAsYouEarn.format)

  private def startPaymentJourney[Spj <: SpjRequest](url: URL, spjRequest: Spj)(spjRequestWrites: Writes[Spj])(implicit headerCarrier: HeaderCarrier): Future[SpjResponse] = {
    httpClientV2.post(url)
      // We add random x-session-id as pay-api requires it. We update it in pay-api after finding journey in pay-frontend via traceId.
      .setHeader("x-session-id" -> UUID.randomUUID().toString)
      .withBody(Json.toJson(spjRequest)(spjRequestWrites))
      .execute[SpjResponse]
  }
}
