FROM gradle:8.12.1-jdk17 AS build
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon
RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
