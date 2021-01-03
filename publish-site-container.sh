#!/bin/bash
if [[ $1 == "" ]]; then
  TAG="latest"
else
  TAG="$1"
fi

./gradlew jar
docker build --tag adamratzman/site:"$TAG" .
docker push adamratzman/site:"$TAG"