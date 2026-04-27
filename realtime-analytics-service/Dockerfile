FROM eclipse-temurin:21-jre-jammy

WORKDIR /realtime-analytics-service

COPY target/realtime-analytics-0.0.1-SNAPSHOT.jar /realtime-analytics-service/realtime-analytics-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/realtime-analytics-service/realtime-analytics-service-0.0.1-SNAPSHOT.jar" ]
