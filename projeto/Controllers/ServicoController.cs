using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ServicoController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();

        private readonly ILogger<ServicoController> _logger;

        public ServicoController(ILogger<ServicoController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostServicos")]
        public void PostServicos(string tipo, string descricao, string estado, int id)
        {

            DatabaseOperations.AdicionarServico(tipo, descricao, estado, id);

            return;
        }


        [HttpGet(Name = "GetServicos")]
        public IEnumerable<Servico> Get()
        {
            List<Servico> reply = DatabaseOperations.VisualizarServicos();
            return reply;
        }
    }
}
