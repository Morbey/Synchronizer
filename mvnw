#!/bin/sh
# Maven Wrapper Script

set -e

# Resolve the maven wrapper jar path
MAVEN_WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

# Download maven-wrapper.jar if it doesn't exist
if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    mkdir -p .mvn/wrapper
    curl -o "$MAVEN_WRAPPER_JAR" https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
fi

# Execute Maven
exec java -jar "$MAVEN_WRAPPER_JAR" "$@"
