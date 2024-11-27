using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class RelatoriosController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();

        private readonly ILogger<RelatoriosController> _logger;

        public RelatoriosController(ILogger<RelatoriosController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostRelatorios")]
        public void PostRelatorios(string descricao)
        {

            DatabaseOperations.AdicionarRelatorioDesempenho(descricao);
            return;
        }


        [HttpGet(Name = "GetRelatorios")]
        public IEnumerable<RelatorioDesempenho> Get()
        {
            List<RelatorioDesempenho> reply = DatabaseOperations.VisualizarRelatorioDesempenho();
            return reply;
        }
    }
}
