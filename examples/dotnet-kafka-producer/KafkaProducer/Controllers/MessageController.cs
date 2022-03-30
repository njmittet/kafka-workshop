using KafkaProducer.Infrastrutcure.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Threading.Tasks;

namespace KafkaProducer.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class MessageController : ControllerBase
    {
        private readonly ILogger<MessageController> _logger;
        private readonly IKafkaMessageProducer _producer;

        public MessageController(ILogger<MessageController> logger, IKafkaMessageProducer producer)
        {
            _logger = logger;
            _producer = producer;
        }

        [HttpPost("async")]
        [Consumes("application/json")]
        public async Task<ActionResult> SendMessageAsync(MessageDTO message)
        {
            _logger.LogDebug($"Received message: {message.ToJson()}");
            var messageResult = await _producer.SendMessageAsync(message);

            return Accepted(messageResult);
        }

        [HttpPost]
        [Consumes("application/json")]
        public ActionResult SendMessage(MessageDTO message)
        {
            _logger.LogDebug($"Received message: {message.ToJson()}");
            _producer.SendMessage(message);

            return Accepted();
        }
    }
}
