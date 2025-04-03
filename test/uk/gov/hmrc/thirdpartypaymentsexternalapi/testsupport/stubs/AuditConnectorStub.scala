package uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{equalToJson, exactly, postRequestedFor, urlPathEqualTo, verify}
import org.scalatest.concurrent.Eventually
import play.api.libs.json.JsObject

object AuditConnectorStub extends Eventually {

  val auditUrl: String = "/write/audit"

  def verifyEventAudited(auditType: String, auditEvent: JsObject): Unit = eventually {
    verify(
      postRequestedFor(urlPathEqualTo(auditUrl))
        .withRequestBody(
          equalToJson(s"""{ "auditType" : "$auditType"  }""", true, true)
        )
        .withRequestBody(
          equalToJson(s"""{ "auditSource" : "set-up-payment-plan"  }""", true, true)
        )
        .withRequestBody(
          equalToJson(s"""{ "detail" : ${auditEvent.toString} }""", true, true)
        )
    )
  }

  def verifyNoAuditEvent(): Unit =
    verify(exactly(0), postRequestedFor(urlPathEqualTo(auditUrl)))

}