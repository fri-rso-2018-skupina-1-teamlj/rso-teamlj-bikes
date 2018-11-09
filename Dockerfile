FROM openjdk:8-jre-slim

RUN mkdir /app

WORKDIR /app

ADD ./api/target/bikes-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8082

CMD java -jar bikes-api-1.0.0-SNAPSHOT.jar
