# kotlin-spring-boot-producer

A Kotlin Spring Boot application that demonstrates how to customize the Kafka producer configuration.

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

Send a message by calling the applications REST API: 

```sh
curl -s  http://localhost:9000/message \
-H "Content-Type: application/json" \
-X POST --data '{"message": "Message"}'
```
