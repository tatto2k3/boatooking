FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

FROM openjdk:17.0.1-jdk-slim
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/build/libs/*.jar boatbooking.jar
ENTRYPOINT ["java","-jar", "boatbooking.jar"]
