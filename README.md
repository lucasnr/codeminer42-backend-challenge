# codeminer42-backend-challenge

A RESTful API to store data of survivors in a virus consumed world. Join the fight against the T-Virus!

## How to run

First of all, create a postgresql database named <strong>theresidentzombie</strong>.

```shell
createdb -U postgres theresidentzombie
```

(Note: if you decide to use a different dbname or postgres username, be sure to replace all of the future occurrences)

After the database is created, run the following command to create the database tables and to insert the items

```shell
psql theresidentzombie < import.sql postgres
```

If you decided to use a different postgres port, dbname, username, or if your postgres user password is not <strong>root</strong>, make sure you edit the datasource configuration values in <strong>src/main/resources/application.properties</strong>

Now, to start the Spring Boot application, run:

```shell
mvn spring-boot:run
```

The app should be running in <strong>http://localhost:8080/</strong> and you should be able to see the docs in <strong>http://localhost:8080/swagger-ui.html</strong>

## Running tests

To test the application, just run the following maven command:

```shell
mvn test
```

## Bulding

You can build a standalone jar file with maven. Just run:

```shell
mvn clean package
```

You jar file should be generated at <strong>target/</strong>. You can run it using:

```shell
java -jar target/the-resident-zombie-0.0.1-SNAPSHOT.jar
```

## LivePreview

There is a live preview of this app currently running at [Heroku](https://the-resident-zombie-codeminer.herokuapp.com/swagger-ui.html). Be aware that the application may be idling thus needing a little time to restart.

Get to know more about Codeminer42 at [https://www.codeminer42.com](https://www.codeminer42.com/)
