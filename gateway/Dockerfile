FROM amazoncorretto:11.0.16-alpine
RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow
ENV GATEWAY_LOCATION=/shareit/gateway.jar
COPY target/*.jar $GATEWAY_LOCATION
ENTRYPOINT java -jar $GATEWAY_LOCATION