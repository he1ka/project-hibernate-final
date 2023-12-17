# Final Hibernate Project

Final Hibernate project with Javarush.
Goal: verify query execution performance between doing MySQL queries and fetching data from Redis.

Steps:
- Fetch data using MySQL
- Adding data to Redis cache
- Verify performance between MySQL queries and fetching data from cache

# Technical stack

- Java 18
- Hibernate
- Maven
- MySQL
- Redis
- Docker compose

# Running application

Please execute src/main/java/ua/javarush/olga_o/Main.java

For verify performance between MySQL and Redis, you can execute test file src/main/test/ua/javarush/olga_o/MainTest.java

# Running application with docker

Build docker container
```bash
docker build -t hibernate-final .
```

And then run it 

```bash
docker run hibernate-final
```