FROM openjdk:8-jre-slim
WORKDIR /site
COPY build/libs/adamratzman.com-release.jar ./site.jar
ENTRYPOINT ["java", "-jar", "/site/site.jar"]