FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8081
COPY target/*.jar /mega-core.jar
ENTRYPOINT ["java","-jar","/mega-core.jar","-Xms2G","-Xmx4g","-Djava.security.egd=file:/dev/./urandom"]