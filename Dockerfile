FROM gradle:jdk24 AS build
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle
RUN gradle --version

COPY src ./src

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]