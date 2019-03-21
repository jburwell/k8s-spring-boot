# Pull the base JDK container to build the project
FROM gradle:5.0.0-jdk8-alpine as builder

# Define an environment variable for the root build directory in the build container
ENV BUILD_ROOT=/tmp/jv
RUN mkdir -p $BUILD_ROOT

# Switch the build root and copy over the sources to build
COPY --chown=gradle:gradle . $BUILD_ROOT/

# Use the Gradle wrapper to build the code -- ensuring version consistency between
# developer workstations and the build environment
WORKDIR $BUILD_ROOT
RUN gradle build

# Pull the base JRE container on which we will deploy the app
FROM openjdk:8-jre-alpine

# Define an environment variable for root application directory in the runtime container
ENV APP_HOME=/app

# Copy the fat JAR from the build container artifacts to the runtime container
COPY --from=builder /tmp/jv/build/libs/jv-server-*.jar $APP_HOME/jv-server.jar

# Open port 8080 to connect to the service
EXPOSE 8080/tcp

# Set the root application directory as the working directory and define the command
# run when the runtime container starts ...
WORKDIR $APP_HOME
ENTRYPOINT java -server -jar $APP_HOME/jv-server.jar
