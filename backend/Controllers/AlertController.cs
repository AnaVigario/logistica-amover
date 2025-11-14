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
        public void Post([FromBody] AlertDTO _a) //_ID_users is a string with the ids of the users separated by commas
        {
            try
            {
                Alert a = new Alert
                {
                    description = _a.description,
                    adminID = _a.adminID
                };
                _db.CreateAlert(a, _a.targets);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao criar alerta.");
                throw new Exception("Erro ao criar alerta.");
            }
            return;
        }

        [HttpGet(Name = "GetAlerts")]
        public IEnumerable<Alert> Get()
        {
            try
            {
                List<Alert> reply = _db.GetAlerts();
                return reply;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error fetching alerts");
                throw new Exception("Error fetching alerts", ex);
            }
        }

        [HttpGet("{id}", Name = "GetAlert")]
        public Alert Get(int id)
        {
            try
            {
                Alert reply = _db.GetAlert(id);
                return reply;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error fetching alert with ID {id}", id);
                throw new Exception($"Error fetching alert with ID {id}", ex);
            }
        }

        [HttpPut("{id}", Name = "PutAlert")]
        public void Put(int id, string description, int ID_admin, string _ID_users)
        {
            List<int> ID_users = new List<int>();
            foreach (string s in _ID_users.Split(',').ToList())
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

    public class AlertDTO
    {
        public string description { get; set; }
        public int adminID { get; set; } // User n - 1
        public List<int> targets { get; set; } // List of User IDs
    }
}