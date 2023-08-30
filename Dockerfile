FROM openjdk:17-oracle
ARG JAR_FILE=target/*.jar
COPY ./target/Spring_LabLims.jar Spring_LabLims.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/Spring_LabLims.jar"]