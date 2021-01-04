#!/bin/bash
./gradlew jar
if [[ $1 == "" ]]; then
  TAG="latest"
  docker build -t adamratzman/site:"$TAG" .
else
  TAG="$1"
  docker build -t adamratzman/site:"$TAG" -t adamratzman/site .
fi

docker push adamratzman/site:"$TAG"
docker push adamratzman/site:latest
