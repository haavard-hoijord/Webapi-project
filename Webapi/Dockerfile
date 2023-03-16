FROM eclipse-temurin:17-jdk-jammy as builder

WORKDIR /
COPY .mvn .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw dependency:go-offline
RUN ./mvnw clean install

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=builder /target/*.jar /*.jar
ENTRYPOINT ["java", "-jar", "/*.jar" ]