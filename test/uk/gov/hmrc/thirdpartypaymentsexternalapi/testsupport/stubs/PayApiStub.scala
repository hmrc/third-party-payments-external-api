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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata.PayApiTestData

object PayApiStub {

  private val selfAssessmentPath: String        = s"/pay-api/third-party-software/self-assessment/journey/start"
  private val vatPath: String                   = s"/pay-api/third-party-software/vat/journey/start"
  private val corporationTaxPath: String        = s"/pay-api/third-party-software/corporation-tax/journey/start"
  private val employersPayAsYouEarnPath: String =
    s"/pay-api/third-party-software/employers-pay-as-you-earn/journey/start"

  private def verifyPost(count: Int, url: String): Unit = {
    val uuidRegex: String = """^[0-9a-fA-F]{8}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{4}\b-[0-9a-fA-F]{12}$"""
    WireMock.verify(
      count,
      postRequestedFor(urlEqualTo(url))
        .withHeader("x-session-id", matching(uuidRegex))
    )
  }

  def stubForStartJourneySelfAssessment(): StubMapping = stubFor(
    post(urlPathEqualTo(selfAssessmentPath)).willReturn(
      aResponse()
        .withStatus(Status.OK)
        .withBody(Json.prettyPrint(Json.toJson(PayApiTestData.spjResponse)))
    )
  )

  def stubForStartJourneySelfAssessment5xx(): StubMapping = stubFor(
    post(urlPathEqualTo(selfAssessmentPath)).willReturn(
      aResponse()
        .withStatus(Status.SERVICE_UNAVAILABLE)
    )
  )

  def stubForStartJourneySelfAssessment3xx(): StubMapping = stubFor(
    post(urlPathEqualTo(selfAssessmentPath)).willReturn(
      aResponse()
        .withStatus(Status.MOVED_PERMANENTLY)
    )
  )

  def verifyStartJourneySelfAssessment(count: Int): Unit = verifyPost(count, selfAssessmentPath)

  def stubForStartJourneyVat(): StubMapping = stubFor(
    post(urlPathEqualTo(vatPath)).willReturn(
      aResponse()
        .withStatus(Status.OK)
        .withBody(Json.prettyPrint(Json.toJson(PayApiTestData.spjResponse)))
    )
  )

  def stubForStartJourneyVat5xx(): StubMapping = stubFor(
    post(urlPathEqualTo(vatPath)).willReturn(
      aResponse()
        .withStatus(Status.SERVICE_UNAVAILABLE)
    )
  )

  def verifyStartJourneyVat(count: Int): Unit = verifyPost(count, vatPath)

  def stubForStartJourneyCorporationTax(): StubMapping = stubFor(
    post(urlPathEqualTo(corporationTaxPath)).willReturn(
      aResponse()
        .withStatus(Status.OK)
        .withBody(Json.prettyPrint(Json.toJson(PayApiTestData.spjResponse)))
    )
  )

  def stubForStartJourneyCorporationTax5xx(): StubMapping = stubFor(
    post(urlPathEqualTo(corporationTaxPath)).willReturn(
      aResponse()
        .withStatus(Status.SERVICE_UNAVAILABLE)
    )
  )

  def verifyStartJourneyCorporationTax(count: Int): Unit = verifyPost(count, corporationTaxPath)

  def stubForStartJourneyEmployersPayAsYouEarn(): StubMapping = stubFor(
    post(urlPathEqualTo(employersPayAsYouEarnPath)).willReturn(
      aResponse()
        .withStatus(Status.OK)
        .withBody(Json.prettyPrint(Json.toJson(PayApiTestData.spjResponse)))
    )
  )

  def stubForStartJourneyEmployersPayAsYouEarn5xx(): StubMapping = stubFor(
    post(urlPathEqualTo(employersPayAsYouEarnPath)).willReturn(
      aResponse()
        .withStatus(Status.SERVICE_UNAVAILABLE)
    )
  )

  def verifyStartJourneyEmployersPayAsYouEarn(count: Int): Unit = verifyPost(count, employersPayAsYouEarnPath)

}
