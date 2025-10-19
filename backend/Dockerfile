FROM openjdk:21-jre-slim
ARG JAR_FILE=build/libs/*.jar
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Xms256m", "-Xmx384m", "-jar", "/app.jar"]
