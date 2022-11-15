# Build image
FROM openjdk:17-jdk

RUN mkdir /app
WORKDIR /app
COPY . .
RUN ./mvnw -DskipTests package -Pprod

# Runtime image
FROM openjdk:17-jdk

RUN mkdir /app
WORKDIR /app
COPY --from=0 /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
