using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ServiceController : ControllerBase
    {
        private readonly DatabaseOperations _db;
        private readonly ILogger<ServiceController> _logger;

        public ServiceController(ILogger<ServiceController> logger, DatabaseOperations db)
        {
            _logger = logger;
            _db = db;
        }

        [HttpPost(Name = "PostService")]
        public void Post(string type, string description, string status, int ID)
        {
            _db.CreateService(type, description, status, ID);
            return;
        }

        [HttpGet(Name = "GetServices")]
        public IEnumerable<Service> Get()
        {
            List<Service> reply = _db.GetServices();
            return reply;
        }

        [HttpGet("{id}", Name = "GetService")]
        public Service Get(int id)
        {
            Service reply = _db.GetService(id);
            return reply;
        }

        [HttpPut("{id}", Name = "PutService")]
        public void Put(int id, string type, string description, string status, int ID)
        {
            _db.EditService(id, type, description, status, ID);
            return;
        }

        [HttpDelete("{id}", Name = "DeleteService")]
        public void Delete(int id)
        {
            _db.DeleteService(id);
            return;
        }
    }
}
