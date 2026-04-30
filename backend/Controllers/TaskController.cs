using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;
using projeto.Services;
using Task = projeto.Data.Models.Task;

namespace projeto.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class TaskController : ControllerBase
    {
        private readonly TaskServices _db;
        private readonly ILogger<TaskController> _logger;

        public TaskController(ILogger<TaskController> logger, TaskServices db)
        {
            _logger = logger;
            _db = db;
        }

        [HttpPost]
        public IActionResult Post([FromBody] TaskDTO _t)
        {
            try
            {
                Task t = new Task
                {
                    type = _t.Type,
                    description = _t.Description,
                    serviceID = _t.ServiceID,
                    clientID = _t.ClientID,
                    status = "Unassigned",
                    creationDate = DateTime.UtcNow
                };
                _db.CreateTask(t, _t.ServiceID, _t.ClientID);
                return Ok(new { message = "Tarefa criada com sucesso." });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro a criar tarefa.");
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        [HttpGet]
        public ActionResult<IEnumerable<Task>> Get()
        {
            try
            {
                List<Task> targets = _db.GetTasks();
                if (targets == null || targets.Count == 0)
                {
                    return NotFound("Nenhuma tarefa encontrada.");
                }
                return Ok(targets);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter tarefas.");
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        [HttpGet("{id}")]
        public ActionResult<Task> Get(int id)
        {
            try
            {
                var target = _db.GetTaskByID(id);
                return target == null ? NotFound("Nenhuma tarefa encontrada com o ID especificado") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter tarefa com ID {ID}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            try
            {
                return _db.DeleteTask(id) ? Ok(new { message = "Tarefa eliminada com sucesso." }) : NotFound("Tarefa não encontrada.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar tarefa com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        [HttpPost("{id}/node/{nodeID}")]
        public IActionResult SetNode(int id, int nodeID)
        {
            try
            {
                return _db.AddTaskNode(id, nodeID) ? Ok(new { message = "Nó adicionado com sucesso." }) : NotFound("Tarefa ou nó não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao adicionar nó.");
                return StatusCode(500, "Erro interno.");
            }
        }

        [HttpDelete("{id}/node/{nodeID}")]
        public IActionResult RemoveNode(int id, int nodeID)
        {
            try
            {
                return _db.RemoveTaskNode(id, nodeID) ? Ok(new { message = "Nó removido com sucesso." }) : NotFound("Tarefa ou nó não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao remover nó.");
                return StatusCode(500, "Erro interno.");
            }
        }

        public class TaskDTO
        {
            public string Type { get; set; } = "";
            public string Description { get; set; } = "";
            public int ServiceID { get; set; }
            public int ClientID { get; set; }
        }
    }
}