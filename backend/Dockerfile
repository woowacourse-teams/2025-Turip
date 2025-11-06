FROM eclipse-temurin:21-jdk
COPY otel/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Xms256m", "-Xmx384m", "-jar", "/app.jar"]
