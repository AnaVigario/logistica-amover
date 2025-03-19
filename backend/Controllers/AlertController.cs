using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AlertController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();
        private readonly ILogger<AlertController> _logger;

        public AlertController(ILogger<AlertController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostAlert")]
        public void Post(string description, int ID_admin, string _ID_users) //_ID_users is a string with the ids of the users separated by commas
        {
            List<int> ID_users = new List<int>();
            foreach(string s in _ID_users.Split(',').ToList())
            {
                ID_users.Add(Convert.ToInt32(s));
            };
            DatabaseOperations.CreateAlert(description, ID_admin, ID_users);
           
            return;
        }

        [HttpGet(Name = "GetAlerts")]
        public IEnumerable<Alert> Get()
        {
            List<Alert> reply = DatabaseOperations.GetAlerts();     
            return reply; 
        }

        [HttpGet("{id}", Name = "GetAlert")]
        public Alert Get(int id)
        {
            Alert reply = DatabaseOperations.GetAlert(id);
            return reply;
        }

        [HttpPut("{id}", Name = "PutAlert")]
        public void Put(int id, string description, int ID_admin, string _ID_users)
        {
            List<int> ID_users = new List<int>();
            foreach(string s in _ID_users.Split(',').ToList())
            {
                ID_users.Add(Convert.ToInt32(s));
            };
            DatabaseOperations.EditAlert(id, description, ID_admin, ID_users);
            return;
        }

        [HttpDelete("{id}", Name = "DeleteAlert")]
        public void Delete(int id)
        {
            DatabaseOperations.DeleteAlert(id);
            return;
        }
    }
}
