FROM openjdk:8-jre-alpine

ENV VERTICLE_FILE messenger-service-1.0-SNAPSHOT-fat.jar

ENV VERTICLE_HOME /usr/verticles

COPY target/$VERTICLE_FILE $VERTICLE_HOME/

WORKDIR target/${VERTICLE_FILE} ${VERTICLE_HOME}/
ENTRYPOINT [ "sh", "-c" ]
CMD [ "exec java -jar ${VERTICLE_FILE}" ]