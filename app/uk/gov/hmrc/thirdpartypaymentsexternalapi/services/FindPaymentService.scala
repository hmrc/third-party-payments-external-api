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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.thirdpartypaymentsexternalapi.connectors.OpenBankingConnector
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FindPaymentService @Inject() (
  openBankingConnector: OpenBankingConnector
)(implicit executionContext: ExecutionContext) {

  def findJourneyByClientId(
    clientJourneyId: ClientJourneyId
  )(implicit hc: HeaderCarrier): Future[ThirdPartySoftwareFindByClientIdResponse] =
    openBankingConnector.findJourneyByClientId(clientJourneyId).map { response =>
      response.copy(taxRegime = camelToPascalCase(response.taxRegime))
    }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private[services] def camelToPascalCase(camelCase: String): String = {
    if (camelCase.isEmpty) camelCase
    else camelCase.head.toUpper.toString + camelCase.tail
  }

}
