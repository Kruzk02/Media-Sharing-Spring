FROM gradle:jdk24-alpine AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

RUN ./gradlew dependencies --no-daemon || return 0

COPY . ./

CMD ["./gradlew", "bootRun"]