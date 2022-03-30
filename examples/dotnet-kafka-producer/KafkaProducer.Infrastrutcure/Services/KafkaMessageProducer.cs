using System;
using System.Threading.Tasks;
using Confluent.Kafka;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace KafkaProducer.Infrastrutcure.Services
{
    public interface IKafkaMessageProducer
    {
        void SendMessage(MessageDTO message);

        Task<DeliveryResult<Null, MessageDTO>> SendMessageAsync(MessageDTO message);

    }

    public class KafkaMessageProducer : IKafkaMessageProducer
    {
        private readonly IConfiguration _config;
        private readonly ILogger<KafkaMessageProducer> _logger;

        private IProducer<Null, MessageDTO> _producer;
        private ProducerConfig producerConfig;

        public KafkaMessageProducer(IConfiguration config, ILogger<KafkaMessageProducer> logger)
        {
            _config = config;
            _logger = logger;

            producerConfig = new ProducerConfig
            {
                BootstrapServers = _config["Kafka:BootstrapServer"],
                EnableSslCertificateVerification = false,
                RetryBackoffMs = 10,
                MessageSendMaxRetries = 10,
                Acks = Acks.All,
            };

            InitiateProducer();
        }

        private void InitiateProducer()
        {
            _producer = new ProducerBuilder<Null, MessageDTO>(producerConfig)
                .SetValueSerializer(new MessageDTOSerializer())
                .Build();
        }

        public void SendMessage(MessageDTO message)
        {
            var kafkaMessage = new Message<Null, MessageDTO>()
            {
                Value = message
            };
            _logger.LogDebug($"Pushing message to Kafka synchronously: {message.ToJson()}");

            _producer.Produce(_config["Kafka:Topic"], kafkaMessage);

            return;
        }

        public async Task<DeliveryResult<Null, MessageDTO>> SendMessageAsync(MessageDTO message)
        {
            var kafkaMessage = new Message<Null, MessageDTO>()
            {
                Value = message
            };
            this._logger.LogDebug($"Pushing message to Kafka asynchronously: {message.ToJson()}");

            var result = await _producer.ProduceAsync(_config["Kafka:Topic"], kafkaMessage);
            _logger.LogDebug($"Message status: {result.Status}");

            return result;
        }
    }
}
