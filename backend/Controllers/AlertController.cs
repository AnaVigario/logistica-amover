using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class AlertController : ControllerBase
    {
        private readonly DatabaseOperations _db;
        private readonly ILogger<AlertController> _logger;

        public AlertController(ILogger<AlertController> logger, DatabaseOperations db)
        {
            _logger = logger;
            _db = db;
        }

        [HttpPost(Name = "PostAlert")]
        public void Post(string description, int ID_admin, string _ID_users) //_ID_users is a string with the ids of the users separated by commas
        {
            List<int> ID_users = new List<int>();
            foreach(string s in _ID_users.Split(',').ToList())
            {
                ID_users.Add(Convert.ToInt32(s));
            };
            _db.CreateAlert(description, ID_admin, ID_users);
           
            return;
        }

        [HttpGet(Name = "GetAlerts")]
        public IEnumerable<Alert> Get()
        {
            List<Alert> reply = _db.GetAlerts();     
            return reply; 
        }

        [HttpGet("{id}", Name = "GetAlert")]
        public Alert Get(int id)
        {
            Alert reply = _db.GetAlert(id);
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
            _db.EditAlert(id, description, ID_admin, ID_users);
            return;
        }

        [HttpDelete("{id}", Name = "DeleteAlert")]
        public void Delete(int id)
        {
            _db.DeleteAlert(id);
            return;
        }
    }
}
