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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId

object OpenBankingStub {

  private def openBankingFindJourneyByClientIdPath(clientJourneyId: ClientJourneyId): String =
    s"/open-banking/payment/search/third-party-software/${clientJourneyId.value.toString}"

  def stubForFindJourneyByClientId(clientJourneyId: ClientJourneyId): StubMapping = stubFor(
    get(urlPathEqualTo(openBankingFindJourneyByClientIdPath(clientJourneyId))).willReturn(
      aResponse()
        .withStatus(Status.OK)
        .withBody("""{
                    |  "clientJourneyId" : "aef0f31b-3c0f-454b-9d1f-07d549987a96",
                    |  "paymentReference" : "paymentRef",
                    |  "taxRegime" : "taxRegime",
                    |  "amountInPence" : 1234,
                    |  "paymentStatus" : "InProgress"
                    |}""".stripMargin)
    )
  )

  def stubForFindJourneyByClientId5xx(): StubMapping = stubFor(
    get(urlPathMatching("/open-banking/payment/search/third-party-software/.*")).willReturn(
      aResponse()
        .withStatus(Status.SERVICE_UNAVAILABLE)
    )
  )

  def stubForFindJourneyByClientId404(): StubMapping = stubFor(
    get(urlPathMatching("/open-banking/payment/search/third-party-software/.*")).willReturn(
      aResponse()
        .withStatus(Status.NOT_FOUND)
    )
  )

}
