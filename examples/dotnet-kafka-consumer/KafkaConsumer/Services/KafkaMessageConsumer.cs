
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Threading;
using System.Threading.Tasks;

namespace KafkaConsumer.Services
{
    public class KafkaMessageConsumer : BackgroundService
    {
        public static List<MessageDTO> resultList;
        private readonly IConfiguration _config;
        private readonly ILogger<KafkaMessageConsumer> _logger;

        public KafkaMessageConsumer(IConfiguration config, ILogger<KafkaMessageConsumer> logger)
        {
            _config = config;
            _logger = logger;
        }

        protected override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            return Task.Run(() => ProcessQueue(stoppingToken), stoppingToken);

        }

        private ConsumeResult<Null, MessageDTO> ProcessQueue(CancellationToken stoppingToken)
        {
            var topic = _config.GetValue<string>("Kafka:Topic");
            var bootstrapServer = _config.GetValue<string>("Kafka:BootstrapServer");

            var config = new ConsumerConfig
            {
                GroupId = "dotnet-kafka",
                BootstrapServers = bootstrapServer,
                EnableSslCertificateVerification = false,
                AutoOffsetReset = AutoOffsetReset.Latest,
                EnableAutoCommit = false,
            };
            _logger.LogInformation($"Starting KafaConsumer on topic: '{topic}' with bootstrap server: '{bootstrapServer}'...");
            using (var consumer = 
                new ConsumerBuilder<Null, MessageDTO>(config)
                .SetErrorHandler((_, e) =>
                {
                    _logger.LogError(e.ToString());
                })
                .SetPartitionsAssignedHandler((c, partitions) =>
                {
                    foreach (var partition in partitions)
                    {
                        _logger.LogInformation($"Consuming messages on partition: '{partition.Partition.Value}' topic: '{partition.Topic}'");
                    }
                })
                .SetValueDeserializer(new MessageDTODeSerializer())
                .Build())
            {
                consumer.Subscribe(topic);
                try
                {
                    if (resultList == null)
                    {
                        resultList = new List<MessageDTO>();
                    }
                    while(!stoppingToken.IsCancellationRequested)
                    {
                        var result = consumer.Consume(stoppingToken);
                        _logger.LogInformation($"Consumed message '{result.Message.Value.Message}' on partition: '{result.Partition.Value}' at offset: '{result.TopicPartitionOffset.Offset}'");
                        if(stoppingToken.IsCancellationRequested)
                        {
                            _logger.LogWarning("Kafka consumer stopped");
                        }
                    }
                }
                catch (ConsumeException ex)
                {
                    _logger.LogError($"Error occured: {ex.Error.Reason}");
                    consumer.Close();
                }
                catch (Exception ex)
                {
                    _logger.LogError($"Error occured: {ex.StackTrace}");
                    consumer.Close();
                }

                return null;
            }
        }
    }
}
