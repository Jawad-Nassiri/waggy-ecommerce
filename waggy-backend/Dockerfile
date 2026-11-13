FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY target/*.jar app.jar
COPY uploads/ /app/uploads/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]