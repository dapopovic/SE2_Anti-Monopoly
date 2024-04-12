FROM openjdk:17
#FROM maven:3.8.5-openjdk-17
LABEL authors="ottzoeke"

WORKDIR /Server
#COPY . .
COPY /target/Server-0.0.1-SNAPSHOT.jar ServerApplication.jar
#RUN mvn clean install
VOLUME /tmp

ENTRYPOINT ["java","-jar","ServerApplication.jar"]
#CMD mvn spring-boot:run
EXPOSE 53215