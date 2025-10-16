# Scientific Calculator

Production-ready Java 17 Spring Boot scientific calculator.

## Build & Run

```bash
mvn clean package
java -jar target/scientific-calculator-1.0.0.jar
```

Respects `PORT` env var and binds to `0.0.0.0` when `BIND_ADDRESS` is set.

## API

- POST `/api/calc/eval` `{ expression, angleMode }` -> `{ result, error }`
- POST `/api/calc/memory` `{ op, value? }` -> `{ memory }`

## Sample checks

- sin(30) in DEG = 0.5
- ln(e) = 1
- log10(1000) = 3
- 3+4×2/(1−5)^2 = 3.5
- 5! = 120
- 2^10 = 1024

## Frontend

Single-page UI in `src/main/resources/templates/index.html` with keyboard support and PWA assets.

## Deploy

### Render (Docker)

1. Push repo to GitHub.
2. Create a new Web Service in Render using this repo.
3. Use the Dockerfile; Render will detect `EXPOSE 8080`.
4. Ensure the service port is set to 8080.

### Railway (Docker or Jar)

- Docker: connect repo, deploy with Dockerfile. Railway sets `PORT` automatically.
- Jar: build locally, upload artifact, run `java -jar target/scientific-calculator-1.0.0.jar`.

### AWS Elastic Beanstalk (Java SE)

1. Build: `mvn package`.
2. Platform: Java SE 17.
3. Upload the jar and include `Procfile` with: `web: java -jar target/scientific-calculator-*.jar --server.port=$PORT`.

## Docker

```bash
mvn -q -DskipTests package

docker build -t sci-calc .

docker run -e PORT=8080 -p 8080:8080 sci-calc
```

