#!/bin/bash
#
# Bash shell script to determine the uptime of a service by verifying the
# HTTP response code of the /actuator/health endpoint (expected status is 200.
#
# Usage ./health-check.sh PORT -> ./health-check.sh 8080
#
# To fail the health check after X seconds, wrap the execution of this script with timeout
# e.g. timeout 30 ./.github/workflows/health-check.sh 8080

# Bash 'strict mode' see http://redsymbol.net/articles/unofficial-bash-strict-mode/
set -euo pipefail
IFS=$'\n\t'

PORT=$1
HEALTH_ENDPOINT="localhost:$PORT/actuator/health"
RETRY_TIMEOUT_SECONDS=5

while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' $HEALTH_ENDPOINT)" != "200" ]];
do
  echo "Service not healthy yet ($HEALTH_ENDPOINT). Trying again in $RETRY_TIMEOUT_SECONDS second(s)"
  sleep $RETRY_TIMEOUT_SECONDS
done

echo "Service is up- and running at $HEALTH_ENDPOINT."
