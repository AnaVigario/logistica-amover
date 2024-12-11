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
    }
}
