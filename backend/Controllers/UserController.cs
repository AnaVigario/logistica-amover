using Microsoft.AspNetCore.Mvc;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UserController : ControllerBase
    {
        private static DatabaseOperations db = new DatabaseOperations();
        private readonly ILogger<UserController> _logger;

        public UserController(ILogger<UserController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostUser")]
        public void Post(string name, string email, string password, string role)
        {
            db.CreateUser(name, email, password, role);
            return;
        }

        [HttpGet(Name = "GetUsers")]
        public IEnumerable<Data.Models.User> Get()
        {
            List<Data.Models.User> reply = db.GetUsers();
            return reply;
        }

        [HttpGet("{id}", Name = "GetUser")]
        public Data.Models.User Get(int id)
        {
            Data.Models.User reply = db.GetUser(id);
            return reply;
        }

        [HttpPut(Name = "UpdateUser")]
        public void Put(int ID, string name, string email, string password, string role)
        {
            db.EditUser(ID, name, email, password, role);
            return;
        }

        [HttpDelete(Name = "DeleteUser")]
        public void Delete(int ID)
        {
            db.DeleteUser(ID);
            return;
        }
    }
}
