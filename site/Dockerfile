FROM openjdk:17-slim-bullseye as builder

COPY . /app

RUN cp /app/src/main/resources/docker-application.yml /app/src/main/resources/application.yml

WORKDIR /app

RUN ./mvnw --batch-mode package -DskipTests

FROM openjdk:17-slim-bullseye

COPY --from=builder /app/target/bbr5-*.jar /bbr5.jar

CMD ["java" , "-Djava.security.egd=file:/dev/./urandom", "-jar", "/bbr5.jar"]
