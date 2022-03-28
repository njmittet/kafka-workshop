# kotlin-spring-kafka-consumer

A Kotlin Spring Boot application that uses [Spring Kafka](https://spring.io/projects/spring-kafka) to demonstrate how to customize the Kafka consumer configuration.

## Usage

Set the address to Kafka in a environmmental variable:

```sh
export KAFKA_BROKERS=localhost:9092 
```

If you are running the application from IntelliJ (or any other IDE), the variable must be set in the Run Configuration.

Run the application with:

```sh
./gradlew bootRun  
```
