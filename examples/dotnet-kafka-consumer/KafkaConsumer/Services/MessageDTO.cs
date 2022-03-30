using System.Text.Json;

namespace KafkaConsumer.Services
{
    public class MessageDTO
    {
        public string Message { get; set; }
        public string ToJson()
        {
            return JsonSerializer.Serialize(this);
        }
    }
}
