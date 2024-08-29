FROM openjdk:17
WORKDIR /usr/app
COPY ./sql/create-schema.sql /docker-entrypoint-initdb.d/1_create.sql
COPY ./build/libs/projeto-0.0.1-SNAPSHOT.jar /usr/app
ENTRYPOINT ["java", "-jar", "projeto-0.0.1-SNAPSHOT.jar"]