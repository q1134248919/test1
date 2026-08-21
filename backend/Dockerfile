FROM mirror.ccs.tencentyun.com/library/maven:3.9-eclipse-temurin-8 AS builder
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN mkdir -p /data/family/uploads
COPY --from=build /src/target/family-assistant-1.0.0.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
VOLUME /data/family/uploads
ENTRYPOINT ["java", "-jar", "app.jar"]
