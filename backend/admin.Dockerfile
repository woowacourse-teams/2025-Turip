FROM eclipse-temurin:21-jdk
COPY otel/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ARG JAR_FILE=turip-admin/build/libs/*.jar
COPY ${JAR_FILE} /turip-admin.jar
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java -javaagent:/opentelemetry-javaagent.jar -Duser.timezone=Asia/Seoul -Xms${JAVA_XMS:-256m} -Xmx${JAVA_XMX:-384m} -jar /turip-admin.jar"]
