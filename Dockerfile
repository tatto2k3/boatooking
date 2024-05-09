FROM gradle:7.2.0-jdk17 AS build
COPY . .
RUN gradle build

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /build/libs/BoatBooking-0.0.1-SNAPSHOT.jar boatbooking.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", "boatbooking.jar"]