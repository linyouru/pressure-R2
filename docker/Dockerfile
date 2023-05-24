FROM openjdk:8
MAINTAINER Linyouru
VOLUME /data/pressure2/execute
WORKDIR /home/pressure2execute
ADD pressure-R2-0.0.1-SNAPSHOT.jar app.jar
CMD mkdir /home/pressure2execute/config
EXPOSE 9320
ENTRYPOINT java -Xms5G -Xmx5G -jar app.jar --spring.config.location=/home/pressure2execute/config/application.yml