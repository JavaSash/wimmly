FROM bellsoft/liberica-openjdk-debian:17
WORKDIR /app
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", \
"-XX:+UseContainerSupport", \
"-XX:MaxRAMPercentage=75.0", \
"-XX:+UseG1GC", \
"-jar", \
"/app/app.jar" ]