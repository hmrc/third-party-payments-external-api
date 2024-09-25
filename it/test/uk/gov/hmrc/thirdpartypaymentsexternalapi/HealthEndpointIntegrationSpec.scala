package uk.gov.hmrc.thirdpartypaymentsexternalapi

import play.api.libs.ws.WSClient

class HealthEndpointIntegrationSpec extends ItSpec {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl = s"http://localhost:$port"

  "service health endpoint" should {
    "respond with 200 status" in {
      val response =
        wsClient
          .url(s"$baseUrl/ping/ping")
          .get()
          .futureValue

      response.status shouldBe 200
    }
  }
}
