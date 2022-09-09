FROM postgres:14-alpine
RUN apk add --no-cache tzdata
ENV TZ=Europe/Moscow