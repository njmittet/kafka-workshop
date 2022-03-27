# kafka-workshop

A `docker-compose` project that exposes Apache Kafka on port 9092 and provides a wrapper configuration around the [edenhill/kcat](https://github.com/edenhill/kcat) Docker image.

This repository is created for a [miles.no](https://www.miles.no) Kafka Workshop, but should be valuable for everyone that wants to check out Apache Kafka.

## Requirements

For the instructions in this repo to work, you will need:

1. A working [Docker](docker) installation
2. A working [docker-compose](https://docs.docker.com/compose/) installation.
3. The `bin/kafka` scripts on your path (optional, but recommended).

### Clone Repository

1. Clone this repository: `git clone https://github.com/nilsjorgen/docker-kafka-kcat.git`
2. `cd` to the cloned folder.
3. Run `source ./environment.txt`
4. Start Kafka cluster by executing the `docker-compose.yaml` file: `'docker-compose up -d`
5. Verify that Kcat is  working:

```sh
$ kcat -L

Metadata for all topics (from broker 1: kafka:29092/1):
 1 brokers:
  broker 1 at kafka:29092 (controller)
 0 topics:
```

### Kafka Scripts

1. Download Kafka: [https://www.apache.org/dyn/closer.cgi?path=/kafka/3.1.0/kafka_2.13-3.1.0.tgz](https://www.apache.org/dyn/closer.cgi?path=/kafka/3.1.0/kafka_2.13-3.1.0.tgz)
2. Extract Kafka with `tar -xvzf kafka_2.13-3.1.0.tgz`
3. Preferably, put `kafka_2.13-3.1.0/bin` on your `$path`.

## Usage

For a quick introduction, have a look at [Learn how to use Kcat](https://dev.to/de_maric/learn-how-to-use-kafkacat-the-most-versatile-kafka-cli-client-1kb4) or the [Kcat Documentation](https://docs.confluent.io/platform/current/app-development/kafkacat-usage.html).

Kcat options:

| Action           | Option                                    |
| -----------------|-------------------------------------------|
| `-P`              | Produce                                   |
| `-C`              | Consume                                   |
| `-t <topic>`      | Topic                                     |
| `-K <delimiter>`  | Delimiter when sending key-value messages |
| `-e`              | Exit when finished (Consumer only)        |
| `-it`             | TTY-mode (default)                        |
| `-i`              | No TTY-mode (when overriding `-t`)        |
| `-f <format>`     | Format the output                         |
| `-o <offset>`     | Read from offset                          |
| `-p <partition>` | Read from partition                       |

Kcat does mode auto-selecting, so using -P or -C to override is actually optional.

### Sending

Send text messages:

```sh
$ kcat -P -t workshop.messages
My First Message  # Enter
My Second Message # Enter
# Ctrl-D to finish
```

### Receiving

Receive all messages on a topic and finish:

```sh
$ kcat -C -t workshop.messages -e
My First Message 
My Second Message 
% Reached end of topic workshop.messages [0] at offset 2: exiting
```

If the `-e` is omitted, the consumer will continue to wait for the next message.

## Batch Mode Sending

In the background, Kcat is a Docker image started with the `-it` flag which opens an interactive shell *in* the running container.

It is also possible to send messages from a file by making Kcat read from stdin:

```sh
$ cat messages.txt | kcat -i -P -t workshop.messages
```

The example above vil trigger a `batch mode` send, which has its own implications (and configuration parameters), so it is also possible to send the messages in a file one by one by looping over them:

When reading files from stdin the `-t` **must** be used in order to turn of TTY, and it **has to be the first parameter**.

```sh
IFS='\n' # Set Internal Field Separator to avoid spaces to behave as newline (might depend on the shell).

for i in $(cat messages.txt); do echo $i | kcat -i -P -t workshop.messages; done
```

## Message as Key-Value Pairs

Sending messages with a key and a value, which is what usually happens when interacting with Kafka programmatically, as in using an SDK.

> Keys are used to determine the partition within a log to which a message get's appended to.
> While the value is the actual payload of the message. Specifying the key so that all
> messages on the same key go to the same partition is important for ordering of messages,
> as well as delete semantics and compaction of topics.

```sh
$ kcat -P -t workshop.messages -K:

1:{"Key": "Value"} # Enter
# Ctrl-D
```

```sh
$ kcat -C -t workshop.messages -K: -f 'Key: %k - Value: %s\n' -e
Key: 1 - Value: {"Key": "Value"}
% Reached end of topic workshop.messages [0] at offset 1: exiting
```

Note that the files in the `data` folder contains messages formatted as key-value pairs:

```txt
1:{"message": "Message 1: Batch 1"}
2:{"message": "Message 2: Batch 1"}
3:{"message": "Message 3: Batch 1"}
4:{"message": "Message 4: Batch 1"}
5:{"message": "Message 5: Batch 1"}
```

Batch send the messages with:

```sh
$ cat data/messages-1.txt | kcat -i -P -t workshop.messages -K:
```

## Partitioned Topics

Use `kafka-topics.sh` to create a topic with three partitions:

```sh
$ kafka-topics.sh --bootstrap-server $KAFKA_BROKERS --create --topic workshop.messages.partitioned --partitions 3
Created topic workshop.messages.partitioned
```

List the topics available on the broker using:

```sh
$ kafka-topics.sh --bootstrap-server $KAFKA_BROKERS --list
workshop.messages.partitioned
workshop.messages
```

Describe the partitioned topic:

```sh
$ kafka-topics.sh --bootstrap-server $KAFKA_BROKERS --describe --topic workshop.messages.partitioned
Topic: workshop.messages.partitioned    Partition: 0    Leader: 1    Replicas: 1    Isr: 1
Topic: workshop.messages.partitioned    Partition: 1    Leader: 1    Replicas: 1    Isr: 1
Topic: workshop.messages.partitioned    Partition: 2    Leader: 1    Replicas: 1    Isr: 1
```

Send multiple messages to the topic

```sh
$ cat data/messages-2.txt | kcat -i -P -t workshop.messages.partitioned -K:
```

Read from all topics (the partitions and offsets are added to the formatting with `-p` and `-o`):

```sh
$ kcat -C -t workshop.messages.partitioned -K: -f '%k [p: %p, o: %o]: %s\n' -e

7 [p: 0, o: 0]: {"id": "uuid-7", "message": "Message 7"}
9 [p: 0, o: 1]: {"id": "uuid-9", "message": "Message 9"}
10 [p: 0, o: 2]: {"id": "uuid-10", "message": "Message 10"}
11 [p: 0, o: 3]: {"id": "uuid-11", "message": "Message 11"}
12 [p: 0, o: 4]: {"id": "uuid-12", "message": "Message 12"}
14 [p: 0, o: 5]: {"id": "uuid-14", "message": "Message 14"}
16 [p: 0, o: 6]: {"id": "uuid-16", "message": "Message 16"}
18 [p: 0, o: 7]: {"id": "uuid-18", "message": "Message 18"}
20 [p: 0, o: 8]: {"id": "uuid-20", "message": "Message 20"}
% Reached end of topic workshop.messages.partitioned [0] at offset 9
6 [p: 1, o: 0]: {"id": "uuid-6", "message": "Message 6"}
15 [p: 1, o: 1]: {"id": "uuid-15", "message": "Message 15"}
19 [p: 1, o: 2]: {"id": "uuid-19", "message": "Message 19"}
8 [p: 2, o: 0]: {"id": "uuid-8", "message": "Message 8"}
13 [p: 2, o: 1]: {"id": "uuid-13", "message": "Message 13"}
17 [p: 2, o: 2]: {"id": "uuid-17", "message": "Message 17"}
% Reached end of topic workshop.messages.partitioned [1] at offset 3
% Reached end of topic workshop.messages.partitioned [2] at offset 3: exiting
```

Note that the messages spread across the partitions, and only in order *within* each partition. Logging is also concurrent.

## Example Applications

The folder [examples](./examples) contains the following exampler (starter) applications:

1. `kotling-spring-boot-consumer`: A Kotlin Spring Boot application that demonstrates how to customize the Kafka consumer configuration.
2. `kotling-spring-boot-producer`: A Kotlin Spring Boot application that demonstrates how to customize the Kafka producer configuration. The application provides a REST API for sending messages.

## Advanced Topics

Compacting:

- [An Introduction to Topic Log Compaction in Apache Kafka](https://medium.com/swlh/introduction-to-topic-log-compaction-in-apache-kafka-3e4d4afd2262)

Insight into how Kafka stores data:

- [A Deep dive into Apache Kafka storage internals](https://strimzi.io/blog/2021/12/17/kafka-segment-retention/)

### Kafka Configuration

An insight into the vast number Kafka configuration parameters:

- [Kafka Broker Configuration](https://kafka.apache.org/081/documentation.html#configuration)
- [Optimizing Kafka Broker Configuration](https://strimzi.io/blog/2021/06/08/broker-tuning/)
- [Optimizing Kafka Producers](https://strimzi.io/blog/2020/10/15/producer-tuning/)
- [Optimizing Kafka Consumers](https://strimzi.io/blog/2021/01/07/consumer-tuning/)

### Articles

Articles worth reading:

- [Lessons learned from running Kafka at Datadog](https://www.datadoghq.com/blog/kafka-at-datadog/)
- [Time for AI: Flexibility With Traps in Kafka](https://synerise.com/blog/time-for-ai-flexibility-with-traps-in-kafka)
- [How We Process One Billion Events Per Day With Kafka](https://www.metarouter.io/blog-posts/how-we-process-one-billion-events-per-day-with-kafka)
