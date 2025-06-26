using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;
namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TaskController : ControllerBase
    {
        private readonly DatabaseOperations _db;
        private readonly ILogger<TaskController> _logger;

        public TaskController(ILogger<TaskController> logger, DatabaseOperations db)
        {
            _logger = logger;
            _db = db;
        }

        [HttpPost(Name = "PostTask")]
        public void Post(string type, DateTime deadline, string description, string status, int ID, int service, string coordinates) //coordinates is a string with the coordinates separated by |
        {
            List<string> listCoordenadas = coordinates.Split('|').ToList();
            _db.CreateTask(type, deadline, description, status,ID ,service, listCoordenadas);
            return;
        }

        [HttpGet(Name = "GetTasks")]
        public IEnumerable<Data.Models.Task> Get()
        {
            List<Data.Models.Task> reply = _db.GetTasks();
            return reply;
        }

        [HttpGet("{id}", Name = "GetTask")]
        public Data.Models.Task Get(int id)
        {
            Data.Models.Task reply = _db.GetTask(id);
            return reply;
        }

        [HttpPut("{id}", Name = "PutTask")]
        public void Put(int id, string type, DateTime deadline, string description, string status, int ID, int service, string coordinates)
        {
            List<string> listCoordenadas = coordinates.Split('|').ToList();
            _db.EditTask(id, type, deadline, description, status, ID, service, listCoordenadas);
            return;
        }

        [HttpDelete("{id}", Name = "DeleteTask")]
        public void Delete(int id)
        {
            _db.DeleteTask(id);
            return;
        }
    }
}
