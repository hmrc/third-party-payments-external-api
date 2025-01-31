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

package uk.gov.hmrc.thirdpartypaymentsexternalapi

import org.apache.pekko.stream.Materializer
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.test.WireMockSupport

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
    "microservice.services.pay-api.port" -> self.wireMockPort
  )

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .configure(configMap).build()
}
