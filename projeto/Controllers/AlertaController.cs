using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AlertaController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();

        private readonly ILogger<AlertaController> _logger;

        public AlertaController(ILogger<AlertaController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostAlertas")]
        public void PostAlertas(string descricao, int id_admin, string id_users)
        {
            List<int> ids = new List<int>();

            foreach(string s in id_users.Split(',').ToList())
            {
                ids.Add(Convert.ToInt32(s));
            };
            DatabaseOperations.AdicionarAlerta(descricao, id_admin, ids);
           
            return;
        }


        [HttpGet(Name = "GetAlertas")]
        public IEnumerable<AlertaManutencao> Get()
        {
            List<AlertaManutencao> reply = DatabaseOperations.VisualizarAlertas();     
            return reply; 
        }
    }
}
