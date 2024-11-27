using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;
namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TarefaController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();

        private readonly ILogger<TarefaController> _logger;

        public TarefaController(ILogger<TarefaController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostTarefas")]
        public void PostTarefas(string tipo, DateTime DeadLine, string descricao, string estado, int id, int servico, string coordenadas)
        {

            List<string> listCoordenadas = coordenadas.Split('|').ToList();

            DatabaseOperations.AdicionarTarefa(tipo, DeadLine, descricao, estado,id ,servico, listCoordenadas);

            return;
        }


        [HttpGet(Name = "GetTarefas")]
        public IEnumerable<Tarefa> Get()
        {
            List<Tarefa> reply = DatabaseOperations.VisualizarTarefas();
            return reply;
        }
    }
}
