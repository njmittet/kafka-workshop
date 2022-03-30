# .Net Kafka Producer
A REST API that uses the Confluent Kafka nuget package to produce messages.

## Install

[Download .Net core 3.1](https://dotnet.microsoft.com/en-us/download/dotnet/3.1)

## Usage

Open in visual studio or run with:
```dotnet run --project KafkaProducer```

Send a message by calling the applications REST API. Either import the postman collection in this repo or use the following commands.
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