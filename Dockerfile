FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/scientific-calculator-*.jar app.jar
EXPOSE 8080
ENV PORT=8080 BIND_ADDRESS=0.0.0.0
ENTRYPOINT ["java","-jar","/app/app.jar"]

