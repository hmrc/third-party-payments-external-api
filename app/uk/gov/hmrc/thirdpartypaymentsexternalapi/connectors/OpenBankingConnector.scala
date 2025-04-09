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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.connectors

import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse

import java.net.URL
import javax.inject.{Inject, Singleton}
import play.mvc.Http.HeaderNames.AUTHORIZATION
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenBankingConnector @Inject() (
    httpClientV2:   HttpClientV2,
    servicesConfig: ServicesConfig
)(implicit executionContext: ExecutionContext) {

  private val openBankingBaseUrl: String = servicesConfig.baseUrl("open-banking") + "/open-banking"
  private val openBankingAuthToken = servicesConfig.getString("internal-auth.token")

  private def findJourneyByClientIdUrl(clientJourneyId: ClientJourneyId): URL = url"$openBankingBaseUrl/payment/search/third-party-software/${clientJourneyId.value}"

  def findJourneyByClientId(clientJourneyId: ClientJourneyId)(implicit headerCarrier: HeaderCarrier): Future[ThirdPartySoftwareFindByClientIdResponse] =
    httpClientV2
      .get(findJourneyByClientIdUrl(clientJourneyId))
      .setHeader(AUTHORIZATION -> openBankingAuthToken)
      .execute[ThirdPartySoftwareFindByClientIdResponse]

}
