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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport

import com.google.inject.{AbstractModule, Provides, Singleton}
import org.apache.pekko.stream.Materializer
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}
import uk.gov.hmrc.http.test.WireMockSupport
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.services.ClientJourneyIdGeneratorService

import java.util.UUID
import scala.annotation.nowarn
import scala.concurrent.ExecutionContext

trait ItSpec extends AnyFreeSpecLike
  with GuiceOneServerPerSuite
  with ScalaFutures
  with IntegrationPatience
  with WireMockSupport
  with Matchers { self =>

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit lazy val materializer: Materializer = app.materializer

  protected lazy val configMap: Map[String, Any] = Map[String, Any](
    "auditing.enabled" -> false,
    "auditing.traceRequests" -> false,
    "microservice.services.pay-api.port" -> self.wireMockPort,
    "microservice.services.open-banking.port" -> self.wireMockPort,
    "internal-auth.token" -> "wowow"
  )

  lazy val overridesModule: AbstractModule = new AbstractModule {
    @Provides
    @Singleton
    @nowarn // silence "method never used" warning
    def staticClientJourneyIdGenerator(): ClientJourneyIdGeneratorService = new ClientJourneyIdGeneratorService {
      override def nextClientJourneyId(): ClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
    }
  }

  def applicationBuilder(): GuiceApplicationBuilder = new GuiceApplicationBuilder()
    .configure(configMap)
    .overrides(GuiceableModule.fromGuiceModule(overridesModule))

  override def fakeApplication(): Application = applicationBuilder().build()
}
