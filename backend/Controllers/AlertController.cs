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

        [HttpPost(Name = "PostAlerts")]
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
    }
}
