FROM eclipse-temurin:21-jdk
COPY otel/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ARG JAR_FILE=admin/build/libs/*.jar
COPY ${JAR_FILE} /admin.jar
EXPOSE 8081
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Xms256m", "-Xmx384m", "-jar", "/admin.jar"]
