#!/bin/bash
set -eux

attempt_counter=0
max_attempts=10

echo "Verifying Keycloak is starting"

until $(curl --output /dev/null --silent --head --fail http://localhost:8888); do
  if [ ${attempt_counter} -eq ${max_attempts} ]; then
    echo "Max attempts reached"
    exit 1
  fi

  printf '.'
  attempt_counter=$(($attempt_counter + 1))
  sleep 5
done

echo "Keycloak is up- and running"
