FROM gradle:7.3.3-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean package

FROM openjdk:17-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
