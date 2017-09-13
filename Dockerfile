FROM openjdk:8u131-jre-alpine
RUN apk update && apk add jq && apk add bash

ADD target/kieclient-0.0.1-SNAPSHOT-fat.jar /service.jar
#ADD cluster.xml /cluster.xml

ADD realm /realm
ADD docker-entrypoint.sh /docker-entrypoint.sh

WORKDIR /

EXPOSE 5703
EXPOSE 15703

#CMD ["java"]
ENTRYPOINT [ "/docker-entrypoint.sh" ]

