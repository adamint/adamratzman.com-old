#!/bin/bash
if [[ $1 == "true" ]]; then
  SITE_IS_PROD="true"
  SITE_PORT=443
else
  SITE_IS_PROD="false"
  SITE_PORT=8080
fi

if [[ $2 == "" ]]; then
  TAG="latest"
else
  TAG="$1"
fi

docker rm adamratzman-site --force

# Environment variables needed:
# SITE_DB_USER
# SITE_DB_PASS
# SITE_DB_URL_WITH_PORT
# SITE_KEYSTORE_PASSWORD

docker run --name adamratzman-site -ti -d --network site-network -p "$SITE_PORT":"$SITE_PORT" -e DB_USER="$SITE_DB_USER" -e DB_PASS="$SITE_DB_PASS" \
  -e DB_URL_WITH_PORT="$SITE_DB_URL_WITH_PORT" -e IS_PROD="$SITE_IS_PROD" -e KEYSTORE_PASSWORD="$SITE_KEYSTORE_PASSWORD" \
  -v "$HOME/ssl":/ssl adamratzman/site:"$TAG"
