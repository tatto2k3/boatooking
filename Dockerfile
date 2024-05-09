FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean build -x test

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /app/build/libs/BoatBooking-0.0.1-SNAPSHOT.jar boatbooking.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "boatbooking.jar"]
