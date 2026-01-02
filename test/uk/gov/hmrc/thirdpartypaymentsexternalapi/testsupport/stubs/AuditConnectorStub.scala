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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, exactly, post, postRequestedFor, stubFor, urlEqualTo, urlPathEqualTo, verify}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.concurrent.Eventually
import play.api.libs.json.JsObject

object AuditConnectorStub extends Eventually {

  val auditUrl: String       = "/write/audit"
  val auditMergedUrl: String = "/write/audit/merged"

  def verifyEventAudited(auditType: String, auditEvent: JsObject): Unit = eventually {
    verify(
      postRequestedFor(urlPathEqualTo(auditUrl))
        .withRequestBody(
          equalToJson(s"""{ "auditType" : "$auditType"  }""", true, true)
        )
        .withRequestBody(
          equalToJson(s"""{ "auditSource" : "third-party-payments-external-api"  }""", true, true)
        )
        .withRequestBody(
          equalToJson(s"""{ "detail" : ${auditEvent.toString} }""", true, true)
        )
    )
  }

  def verifyNoAuditEvent(): Unit =
    verify(exactly(0), postRequestedFor(urlPathEqualTo(auditUrl)))

  def serviceAvailable(): StubMapping = {
    stubFor(post(urlEqualTo(auditUrl)).willReturn(aResponse().withStatus(200)))
    stubFor(post(urlEqualTo(auditMergedUrl)).willReturn(aResponse().withStatus(200)))
  }
}
