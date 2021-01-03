#!/bin/sh
docker run --name adamratzman-site-mysql-server -e MYSQL_ROOT_PASSWORD="$1" -e MYSQL_DATABASE="site" -e MYSQL_USER="adam" -e MYSQL_PASSWORD="$1" -p 3306:3306 -d mysql
docker network connect site-network adamratzman-site-mysql-server