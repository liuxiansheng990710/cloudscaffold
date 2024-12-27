FROM eclipse-temurin:11.0.13_8-jdk-focal
WORKDIR /opt
ARG WORKSPACE_DIR
COPY ${WORKSPACE_DIR}/java/tls-allowed.java.security ./java/
ARG JAR_FILE
COPY ${WORKSPACE_DIR}/${JAR_FILE} ./app.jar
ENTRYPOINT java $JAVA_OPTS -jar /opt/app.jar
EXPOSE 2103