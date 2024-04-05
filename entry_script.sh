#!/bin/sh
if [ -z "${AWS_LAMBDA_RUNTIME_API}" ]; then
  exec /usr/local/bin/aws-lambda-rie /usr/bin/java -cp '/opt/app/app.jar' com.amazonaws.services.lambda.runtime.api.client.AWSLambda $@
else
  exec /usr/bin/java -cp '/opt/app/app.jar' com.amazonaws.services.lambda.runtime.api.client.AWSLambda $@
fi