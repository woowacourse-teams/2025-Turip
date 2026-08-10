FROM eclipse-temurin:21-jdk
COPY otel/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ARG JAR_FILE=turip-app/build/libs/*.jar
COPY ${JAR_FILE} /turip-app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -javaagent:/opentelemetry-javaagent.jar -Duser.timezone=Asia/Seoul ${JAVA_OPTS:--Xms512m -Xmx512m} -jar /turip-app.jar"]
