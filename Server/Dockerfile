FROM openjdk:17
LABEL authors="ottzoeke"

# Add a volume pointing to /tmp
WORKDIR /Server

# Add the application's jar to the container
COPY /target/Server-0.0.1-SNAPSHOT.jar ServerApplication.jar

#RUN mvn clean install
VOLUME /tmp

# Port to expose
EXPOSE 53215

# Start the application's jar file
ENTRYPOINT ["java","-jar","ServerApplication.jar"]