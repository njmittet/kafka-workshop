using Confluent.Kafka;
using System.Text.Json;

namespace KafkaProducer.Infrastrutcure.Services
{
    public class MessageDTOSerializer : ISerializer<MessageDTO>
    {
        public byte[] Serialize(MessageDTO data, SerializationContext context)
        {
            return JsonSerializer.SerializeToUtf8Bytes(data);
        }
    }
}
