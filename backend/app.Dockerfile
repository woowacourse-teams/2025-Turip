FROM eclipse-temurin:21-jdk
COPY otel/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ARG JAR_FILE=turip-app/build/libs/*.jar
COPY ${JAR_FILE} /turip-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Xms256m", "-Xmx384m", "-jar", "/turip-app.jar"]
