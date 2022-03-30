# java-kafka-producer

A Java Spring Boot application that uses
the [Kafka Java Client](https://docs.confluent.io/clients-kafka-java/current/overview.html) to send messages.

## Usage

Set the address to Kafka in a environmental variable:

```sh
export KAFKA_BROKERS=localhost:9092 
```

If you are running the application from IntelliJ (or any other IDE), the variable must be set in the Run Configuration.

Run the application with:

```sh
./gradlew bootRun  
```

Send a message by calling the applications REST API:

```sh
curl -s  http://localhost:9002/message \
-H "Content-Type: application/json" \
--data '{"message": "Message"}'
```

An async message:

```sh
curl -s  http://localhost:9002/message/async \
-H "Content-Type: application/json" \
--data '{"message": "Message"}'
```
