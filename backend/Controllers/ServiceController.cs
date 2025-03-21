using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ServiceController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();
        private readonly ILogger<ServiceController> _logger;

        public ServiceController(ILogger<ServiceController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostService")]
        public void Post(string type, string description, string status, int ID)
        {
            DatabaseOperations.CreateService(type, description, status, ID);
            return;
        }

        [HttpGet(Name = "GetServices")]
        public IEnumerable<Service> Get()
        {
            List<Service> reply = DatabaseOperations.GetServices();
            return reply;
        }

        [HttpGet("{id}", Name = "GetService")]
        public Service Get(int id)
        {
            Service reply = DatabaseOperations.GetService(id);
            return reply;
        }

        [HttpPut("{id}", Name = "PutService")]
        public void Put(int id, string type, string description, string status, int ID)
        {
            DatabaseOperations.EditService(id, type, description, status, ID);
            return;
        }

        [HttpDelete("{id}", Name = "DeleteService")]
        public void Delete(int id)
        {
            DatabaseOperations.DeleteService(id);
            return;
        }
    }
}
