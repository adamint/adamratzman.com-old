#!/bin/bash
if [[ $1 == "true" ]]; then
  SITE_IS_PROD="true"
  SITE_PORT=443
else
  SITE_IS_PROD="false"
  SITE_PORT=8080
fi

# SITE_DB_USER
# SITE_DB_PASS
# SITE_DB_URL_WITH_PORT
# SITE_KEYSTORE_PASSWORD

docker rm adamratzman-site --force

if [[ $2 == "true" ]]; then
  ./gradlew jar
  docker build --tag adamratzman/site .
fi

docker run --name adamratzman-site --network site-network -p "$SITE_PORT":"$SITE_PORT" -e DB_USER="$SITE_DB_USER" -e DB_PASS="$SITE_DB_PASS" \
  -e DB_URL_WITH_PORT="$SITE_DB_URL_WITH_PORT" -e IS_PROD="$SITE_IS_PROD" -e KEYSTORE_PASSWORD="$SITE_KEYSTORE_PASSWORD" \
  -v "$HOME/ssl":/ssl adamratzman/site
