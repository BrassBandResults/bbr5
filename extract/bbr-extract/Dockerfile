FROM openjdk:17-slim-bullseye as builder

COPY . /app

WORKDIR /app

RUN ./mvnw --batch-mode package -DskipTests

FROM openjdk:17-slim-bullseye

COPY --from=builder /app/target/bbr-extract*.jar /bbr-extract.jar

CMD ["java" , "-Djava.security.egd=file:/dev/./urandom", "-jar", "/bbr5-extract.jar"]
