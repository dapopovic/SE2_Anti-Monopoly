FROM openjdk:17
LABEL authors="ottzoeke"

FROM maven:3.8.5-openjdk-17

WORKDIR /Server
COPY . .
RUN mvn clean install

CMD mvn spring-boot:run