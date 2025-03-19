using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class ReportController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();
        private readonly ILogger<ReportController> _logger;

        public ReportController(ILogger<ReportController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostReport")]
        public void Post(string description)
        {

            DatabaseOperations.CreateReport(description);
            return;
        }

        [HttpGet(Name = "GetReports")]
        public IEnumerable<PerformanceReport> Get()
        {
            List<PerformanceReport> reply = DatabaseOperations.GetReports();
            return reply;
        }

        /*
        [HttpGet("{id}", Name = "GetReport")]
        public PerformanceReport Get(int id)
        {
            PerformanceReport reply = DatabaseOperations.GetReport(id);
            return reply;
        }

        [HttpDelete("{id}", Name = "DeleteReport")]
        public void Delete(int id)
        {
            DatabaseOperations.DeleteReport(id);
            return;
        }
        */
    }
}
