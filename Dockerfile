FROM eclipse-temurin:17-jre-alpine as runtime
WORKDIR /app
COPY target/scientific-calculator-*.jar app.jar
EXPOSE 8080
ENV PORT=8080 BIND_ADDRESS=0.0.0.0
ENTRYPOINT ["java","-jar","/app/app.jar"]

