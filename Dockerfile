FROM openjdk:8u131-jre-alpine 
RUN apk update && apk add jq

ADD target/kie-client-0.0.1-SNAPSHOT-fat.jar /opt/kie-client-0.0.1-SNAPSHOT-fat.jar

ADD realm /opt/realm
ADD docker-entrypoint2.sh /opt/docker-entrypoint2.sh
ADD cluster.xml /opt/cluster.xml

WORKDIR /opt

EXPOSE 8080
ENTRYPOINT [ "/opt/docker-entrypoint2.sh" ]

