FROM openjdk:21-jdk
RUN curl -L -o /opentelemetry-javaagent.jar \
  https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Xms256m", "-Xmx384m", "-Dspring.profiles.active=dev", "-jar", "/app.jar"]
