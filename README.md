
# third-party-payments-external-api
========================

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The third-party-payments-external-api allows users to do two things:
* start a payment journey using the `/pay` endpoint
* lookup the status of a payment journey using the `/status` endpoint

## POST `/pay`
The `/pay` endpoint takes number of parameters as a json body including but not limited to (see [OAS](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/third-party-payments-external-api/1.0) file in developer hub for more detailed information):
* tax type/regime
* amount to pay in pence
* tax reference

And returns a redirect url for the user to navigate to in order to begin a prepopulated payment journey in the `pay-frontend` and `pay-api` microservices. 
It also returns a `clientJourneyId` which can be used to lookup the status of the payment journey.

Example request:
```
curl -X POST http://localhost:10156/pay \
-H 'Content-Type: application/json' \
-d '{
  "taxRegime": "SelfAssessment",
  "reference": "1234567891",
  "amountInPence": 123
}'
```

Example response body:
```json
{
  "clientJourneyId": "aef0f31b-3c0f-454b-9d1f-07d549987a96",
  "redirectURL": "https://tax.gov.uk/payments/start/jhdugiuygdwuygsxajvbh"
}
```

## GET `/status`
The `/status` endpoint takes a query parameter which is a clientJourneyId from a started payment journey (see [OAS](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/third-party-payments-external-api/1.0) file in developer hub for more detailed information).

Example request:
```
curl http://localhost:10156/status\?clientJourneyId=aef0f31b-3c0f-454b-9d1f-07d549987a96
```

Example response:
```json
{
  "clientJourneyId": "aef0f31b-3c0f-454b-9d1f-07d549987a96",
  "paymentJourneyStatus": "InProgress"
}
```

## Requirements

- Scala 2.13.x
- Java 11
- sbt 1.9.x
- [Service Manager V2](https://github.com/hmrc/sm2)

## Running locally

You can run the service locally using the service manager profile `OPS_3PS`

e.g. `sm2 --start OPS_3PS`

[Example requests](https://github.com/hmrc/third-party-payments-external-api/tree/main/api-calls)

## Run Tests

Run unit tests: `sbt test`

Run integration tests: `sbt it:test`

## To view the OAS

To view documentation locally, ensure the Individual Losses API is running, and run api-documentation-frontend:

```
./run_local_with_dependencies.sh
```

Then go to http://localhost:9680/api-documentation/docs/openapi/preview and use the appropriate port and version:

```
http://localhost:10156/api/conf/1.0/application.yaml
```

## API Reference / Documentation

Available on the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/third-party-payments-external-api/1.0)

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
