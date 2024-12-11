﻿using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;
namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class TaskController : ControllerBase
    {
        private static DatabaseOperations DatabaseOperations = new DatabaseOperations();
        private readonly ILogger<TaskController> _logger;

        public TaskController(ILogger<TaskController> logger)
        {
            _logger = logger;
        }

        [HttpPost(Name = "PostTask")]
        public void Post(string type, DateTime deadline, string description, string status, int ID, int service, string coordinates) //coordinates is a string with the coordinates separated by |
        {
            List<string> listCoordenadas = coordinates.Split('|').ToList();
            DatabaseOperations.CreateTask(type, deadline, description, status,ID ,service, listCoordenadas);
            return;
        }

        [HttpGet(Name = "GetTasks")]
        public IEnumerable<Data.Models.Task> Get()
        {
            List<Data.Models.Task> reply = DatabaseOperations.GetTasks();
            return reply;
        }
    }
}