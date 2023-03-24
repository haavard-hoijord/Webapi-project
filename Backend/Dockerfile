FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /
COPY pom.xml ./
COPY src ./src
RUN mvn clean install

FROM eclipse-temurin:17-jre-jammy
EXPOSE 8080
COPY --from=build /target/*.jar /*.jar
ENTRYPOINT ["java", "-jar", "/*.jar" ]