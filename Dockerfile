FROM openjdk:jre-slim
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ENTRYPOINT java -server -Xms${JAVA_HEAP_SIZE} -Xmx${JAVA_HEAP_SIZE} \
  -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled \
  -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark \
  -jar /app.jar
