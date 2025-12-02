using Microsoft.AspNetCore.Mvc;
using projeto.Authentication;
using projeto.Data.Models;
using projeto.Services;

namespace projeto.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class MessageController : ControllerBase
    {
        private readonly VehicleServices _vdb;
        private readonly MessageServices _mdb;
        private readonly ILogger<MessageController> _logger;

        public MessageController(ILogger<MessageController> logger, VehicleServices vdb, MessageServices mdb)
        {
            _logger = logger;
            _vdb = vdb;
            _mdb = mdb;
        }

        [HttpPost("{vin}")]
        [ApiKeyAuth]
        public IActionResult Post(string vin, [FromBody] MessageDTO messageDto)
        {
            try
            {
                // 1. Verificar se o VIN existe
                var vehicle = _vdb.GetVehicleByID(vin);
                if (vehicle == null)
                {
                    return BadRequest(new
                    {
                        status = "invalid_vin",
                        code = 400,
                        description = "O VIN indicado não corresponde a nenhum veículo registado"
                    });
                }

                // 2. Verificar conteúdo da mensagem (exemplo simples)
                if (string.IsNullOrEmpty(messageDto.Message) || string.IsNullOrEmpty(messageDto.Sender))
                {
                     // Embora o PDF não especifique um erro 400 para body inválido, é boa prática.
                     // Vou assumir processamento genérico de erro ou sucesso por enquanto.
                }

                // 3. Registar a mensagem
                var log = new MessageLog
                {
                    VehicleVID = vin,
                    Sender = messageDto.Sender,
                    Content = messageDto.Message,
                    Timestamp = DateTime.UtcNow,
                    Status = "accepted"
                };
                _mdb.CreateMessageLog(log);

                // 4. Retornar sucesso
                return Ok(new
                {
                    status = "accepted",
                    code = 200,
                    description = "Mensagem recebida com sucesso pelo backend e será processada"
                });

            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao processar mensagem para VIN {vin}", vin);
                return StatusCode(500, new
                {
                    status = "processing_error",
                    code = 500,
                    description = "Ocorreu um erro interno ao tentar processar a mensagem recebida."
                });
            }
        }
    }

    public class MessageDTO
    {
        public string Message { get; set; }
        public string Sender { get; set; }
    }
}
