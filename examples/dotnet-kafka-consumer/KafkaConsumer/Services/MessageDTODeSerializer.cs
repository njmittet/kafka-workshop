using Confluent.Kafka;
using Microsoft.Extensions.Logging;
using System;
using System.Text.Json;

namespace KafkaConsumer.Services
{
    public class MessageDTODeSerializer : IDeserializer<MessageDTO>
    {
        public MessageDTO Deserialize(ReadOnlySpan<byte> data, bool isNull, SerializationContext context)
        {
            if(isNull)
            {
                return null;
            }

            return JsonSerializer.Deserialize<MessageDTO>(data);
        }
    }
}
