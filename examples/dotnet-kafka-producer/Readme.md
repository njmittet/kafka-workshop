# .Net Kafka Producer
A REST API that uses the Confluent Kafka nuget package to produce messages.

## Install

[Download .Net core 6.0](https://dotnet.microsoft.com/en-us/download/dotnet/6.0)

## Usage

Open in visual studio or run with:
```dotnet run --project KafkaProducer```

Send a message by calling the applications REST API. Either import the postman collection in this repo or use the following commands. We also added Swagger UI for sending messages from a web interface. 
```sh
curl -s  http://localhost:9000/message \
-H "Content-Type: application/json" \
--data '{"message": "Message"}'
```

Send an async message:

```sh
curl -s  http://localhost:9000/message/async \
-H "Content-Type: application/json" \
--data '{"message": "Message"}'
```