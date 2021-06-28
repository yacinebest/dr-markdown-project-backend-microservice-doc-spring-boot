#docker image build -t drmarkdown-microservice-doc-spring .
FROM openjdk:15
EXPOSE 9991
ADD build/libs/doc-0.0.1-SNAPSHOT.jar doc-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/doc-0.0.1-SNAPSHOT.jar"]