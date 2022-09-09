FROM amazoncorretto:11.0.16-alpine
ENV GATEWAY_LOCATION=/shareit/gateway.jar
COPY target/*.jar $GATEWAY_LOCATION
ENTRYPOINT java -jar $GATEWAY_LOCATION