FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

RUN apk add --no-cache maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM tomcat:10.1-jre21-temurin-jammy
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war

RUN mkdir -p /app/logs && chown -R tomcat:tomcat /app/logs
ENV SPRING_PROFILES_ACTIVE=docker
ENV LOGGING_PATH=/app/logs
ENV CATALINA_OPTS="-Dlogging.path=/app/logs -Xms256m -Xmx512m"

EXPOSE 8080
USER tomcat
CMD ["catalina.sh", "run"]
