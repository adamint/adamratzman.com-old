#!/bin/bash

SESSIONS_ROOT_DIR="$HOME/site-sessions"
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
  TAG="$2"
  docker pull adamratzman/site:"$TAG"
fi

docker pull adamratzman/site

docker rm adamratzman-site --force

# Environment variables needed:
# SITE_DB_USER
# SITE_DB_PASS
# SITE_JDBC_CONNECTION_URL
# SITE_KEYSTORE_PASSWORD
#

docker run --name adamratzman-site -ti -d --network site-network -p "$SITE_PORT":"$SITE_PORT" -e DB_USER="$SITE_DB_USER" -e DB_PASS="$SITE_DB_PASS" \
  -e JDBC_CONNECTION_URL="$SITE_JDBC_CONNECTION_URL" -e IS_PROD="$SITE_IS_PROD" -e KEYSTORE_PASSWORD="$SITE_KEYSTORE_PASSWORD" -e SESSIONS_ROOT_DIR="/sessions" \
  -v "$HOME/ssl":/ssl -v "$SESSIONS_ROOT_DIR":/sessions adamratzman/site:"$TAG"
