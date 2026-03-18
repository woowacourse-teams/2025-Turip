FROM eclipse-temurin:21-jdk
COPY otel/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ARG JAR_FILE=turip-admin/build/libs/*.jar
COPY ${JAR_FILE} /turip-admin.jar
EXPOSE 8081
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-Duser.timezone=Asia/Seoul", "-Xms256m", "-Xmx384m", "-jar", "/turip-admin.jar"]
