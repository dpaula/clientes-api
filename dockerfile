FROM docker.io/library/openjdk:11-jre-slim

ENV USER_NAME userapp

RUN useradd -d /opt -c "User Start App" -s /bin/bash $USER_NAME

VOLUME /tmp

MAINTAINER fplima

ENV PROFILE=

ENV TZ=America/Sao_Paulo

COPY target/clientes-api.jar /opt/app.jar

USER $USER_NAME

WORKDIR /opt

ENTRYPOINT java -Xms128m -Xmx256m -Xss512k -XX:MaxMetaspaceSize=128m -XX:InitialRAMPercentage=25 -XX:MinRAMPercentage=50 -XX:MaxRAMPercentage=80 -Djava.security.egd=file:/dev/./urandom -jar /opt/app.jar $PROFILE

EXPOSE 2550