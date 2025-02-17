package uk.gov.hmrc.thirdpartypaymentsexternalapi.models

import play.api.libs.json.{Format, Json}

final case class ThirdPartySoftwareFindByClientIdResponse(
                                                           clientJourneyId:      ClientJourneyId,
                                                           paymentReference:     String,
                                                           taxRegime:            String,
                                                           amountInPence:        Long,
                                                           paymentJourneyStatus: String
                                                         )
object ThirdPartySoftwareFindByClientIdResponse {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: Format[ThirdPartySoftwareFindByClientIdResponse] = Json.format[ThirdPartySoftwareFindByClientIdResponse]
}
