FROM openjdk:17
RUN mkdir /app
COPY . /app
WORKDIR /app
RUN apt-get update
EXPOSE 8080
CMD ["./gradlew", "installDebug"]
