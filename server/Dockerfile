FROM amazoncorretto:11.0.16-alpine
RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow
ENV SERVER_LOCATION=/shareit/server.jar
COPY target/*.jar $SERVER_LOCATION
ENTRYPOINT java -jar $SERVER_LOCATION