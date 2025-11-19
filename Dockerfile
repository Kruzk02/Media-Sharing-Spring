FROM gradle:jdk24

WORKDIR /app

COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

RUN gradle --version
RUN gradle build -x test --no-daemon || true

COPY src ./src
COPY image ./image

CMD ["gradle", "bootRun", "--no-daemon"]