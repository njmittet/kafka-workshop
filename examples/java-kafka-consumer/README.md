# java-kafka-consumer

A Java Spring Boot application that uses
the [Kafka Java Client](https://docs.confluent.io/clients-kafka-java/current/overview.html) to receive messages.

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
