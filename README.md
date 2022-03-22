# docker-kafka

A `docker-compose` project that exposes Apache Kafka on port 9092 and provides a wrapper configuration around the [edenhill/kcat](https://github.com/edenhill/kcat) Docker image.

## Requirements

For the instructions in this repo to work, you will need:

1. A working [Docker](docker) installation
2. [docker-compose](docker compose)
3. The `bin/kafka` scripts on your path.

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

Basic send and receive of a text message:

```sh
$ kcat -P -t my-topic
My First Message  # Enter
My Second Message # Enter
# Ctrl-D to finish
```

### Receiving

Receive all messages on a topic and finish:

```sh
$ kcat -C -t my-topic -e
My First Message 
My Second Message 
% Reached end of topic my-topic [0] at offset 2: exiting
```

If the `-e` is omitted, the consumer will continue to wait for the next message.

## Two ways of sending

In the background Kcat is a Docker imaged started with the `-it` parameter, which opens a interactive shell in the running container, as in the example above.

It is also possible to send messages from a file by making Kcat read form stdin:

```sh
$ cat messages.txt | kcat -i -P -t my-batch-topic
```

The example above vil trigger a `batch mode` send, which has its own implications (and configuration parameters), so it is also possible to send the messages in a file one by one by looping over them:

```sh
IFS='\n' # Set Internal Field Separator to avoid spaces to behave as newline (might depend on the shell).

for i in $(cat messages.txt); do echo $i | kcat -i -P -t my-topic; done
```

Note, when reading files from stdin the `-t` **must** be used in order to turn of TTY, and it **has to be the first parameter**.

## Message as Key-Value Pairs

Sending messages with a key and a value, which is what usually happens when interacting with Kafka programmatically, as in using an SDK.

> Keys are used to determine the partition within a log to which a message get's appended to.
> While the value is the actual payload of the message. Specifying the key so that all
> messages on the same key go to the same partition is important for ordering of messages,
> as well as delete semantics and compaction of topics.

```sh
$ kcat -P -t my-topic -K:

1:{"Key": "Value"} # Enter
# Ctrl-D
```

```sh
$ kcat -C -t my-topic -K: -f 'Key: %k - Value: %s\n' -e
Key: 1 - Value: {"Key": "Value"}
% Reached end of topic my-topic [0] at offset 1: exiting
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
$ cat data/batch-1.txt | kcat -i -P -t my-topic -K:
```

## Partitioned Topics

Use `kafka-topics.sh` to create a topic with three partitions:

```sh
$ kafka-topics.sh --bootstrap-server $KAFKA_BROKER --create --topic my-partitioned-topic --partitions 3
Created topic my-partitioned-topic.
```

List the topics available on the broker using:

```sh
$ kafka-topics.sh --bootstrap-server $KAFKA_BROKER --list
my-partitioned-topic
my-topic
```

Describe the partitioned topic:

```sh
$ kafka-topics.sh --bootstrap-server $KAFKA_BROKER --describe --topic my-partitioned-topic
Topic: my-partitioned-topic    Partition: 0    Leader: 1    Replicas: 1    Isr: 1
Topic: my-partitioned-topic    Partition: 1    Leader: 1    Replicas: 1    Isr: 1
Topic: my-partitioned-topic    Partition: 2    Leader: 1    Replicas: 1    Isr: 1
```

Send multiple messages to the topic

```sh
$ cat data/batch-3.txt | kcat -i -P -t my-partitioned-topic -K:
```

Read from all topics (the partitions and offsets are added to the formatting with `-p` and `-o`):

```sh
7 [p: 0, o: 0]: {"message": "Message 7: Batch 3"}
9 [p: 0, o: 1]: {"message": "Message 9: Batch 3"}
10 [p: 0, o: 2]: {"message": "Message 10: Batch 3"}
11 [p: 0, o: 3]: {"message": "Message 11: Batch 3"}
12 [p: 0, o: 4]: {"message": "Message 12: Batch 3"}
14 [p: 0, o: 5]: {"message": "Message 14: Batch 3"}
% Reached end of topic my-partitioned-topic [0] at offset 6
2 [p: 1, o: 0]: {"message": "Message 2: Batch 3"}
3 [p: 1, o: 1]: {"message": "Message 3: Batch 3"}
4 [p: 1, o: 2]: {"message": "Message 4: Batch 3"}
5 [p: 1, o: 3]: {"message": "Message 5: Batch 3"}
6 [p: 1, o: 4]: {"message": "Message 6: Batch 3"}
15 [p: 1, o: 5]: {"message": "Message 15: Batch 3"}
% Reached end of topic my-partitioned-topic [1] at offset 6
1 [p: 2, o: 0]: {"message": "Message 1: Batch 3"}
8 [p: 2, o: 1]: {"message": "Message 8: Batch 3"}
13 [p: 2, o: 2]: {"message": "Message 13: Batch 3"}
% Reached end of topic my-partitioned-topic [2] at offset 3: exiting
```

Note that the messages spread across the partitions, and only in order *within* each partition.

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
