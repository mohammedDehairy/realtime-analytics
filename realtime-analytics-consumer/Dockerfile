FROM openjdk:17-jdk-slim

WORKDIR /realtime-analytics-consumer

COPY /target/realtime-analytics-consumer-1.0-SNAPSHOT-jar-with-dependencies.jar /realtime-analytics-consumer/realtime-analytics-consumer-1.0-SNAPSHOT.jar

EXPOSE 9292

ENTRYPOINT [ "java", "-jar", "/realtime-analytics-consumer/realtime-analytics-consumer-1.0-SNAPSHOT.jar" ]