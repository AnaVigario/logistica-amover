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
    public class UserController : ControllerBase
    {
        private readonly UserServices _db;
        private readonly UserTaskServices _taskdb;
        private readonly ILogger<UserController> _logger;

        public UserController(ILogger<UserController> logger, UserServices db, UserTaskServices taskdb)
        {
            _logger = logger;
            _db = db;
            _taskdb = taskdb;
        }

        // --- CRUD DE UTILIZADOR ---

        [HttpPost]
        public IActionResult Post([FromBody] UserDTO user)
        {
            try
            {
                User u = new User
                {
                    name = user.Name,
                    email = user.Email,
                    password = user.Password,
                    role = user.Role ?? "user"
                };

                // Passa o CompanyID para o serviço conforme a alteração no UserServices
                _db.CreateUser(u, user.CompanyID);
                return Ok(new { message = "Utilizador criado com sucesso." });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao criar utilizador.");
                return StatusCode(500, "Erro interno: " + ex.Message);
            }
        }

        [HttpGet]
        public ActionResult<IEnumerable<User>> Get()
        {
            try
            {
                List<User> targets = _db.GetUsers();
                if (targets == null || targets.Count == 0)
                {
                    return NotFound("Nenhum utilizador encontrado.");
                }
                return Ok(targets);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter utilizadores.");
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        [HttpGet("{userID}")]
        public ActionResult<User> Get(int userID)
        {
            try
            {
                var target = _db.GetUserByID(userID);
                return target == null ? NotFound("Utilizador não encontrado") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter utilizador.");
                return StatusCode(500, "Erro interno.");
            }
        }

        [HttpPut("{userID}")]
        public IActionResult Put(int userID, [FromBody] UserDTO user)
        {
            try
            {
                User u = new User
                {
                    ID = userID,
                    name = user.Name,
                    email = user.Email,
                    password = user.Password,
                    role = user.Role ?? "user"
                };
                return _db.EditUser(u) ? Ok(new { message = "Utilizador atualizado com sucesso." }) : NotFound("Utilizador não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao atualizar utilizador.");
                return StatusCode(500, "Erro interno.");
            }
        }

        [HttpDelete("{userID}")]
        public IActionResult Delete(int userID)
        {
            try
            {
                return _db.DeleteUser(userID) ? Ok(new { message = "Utilizador eliminado com sucesso." }) : NotFound("Utilizador não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar utilizador.");
                return StatusCode(500, "Erro interno.");
            }
        }

        // --- ASSOCIAÇÃO DE TAREFAS (Igual à lógica de Nodes no URL) ---

        [HttpPost("{userID}/task/{taskID}")]
        public IActionResult PostTask(int userID, int taskID)
        {
            try
            {
                // Agora os IDs vêm diretamente do URL, sem necessidade de JSON body
                _taskdb.AddTaskToUser(userID, taskID);
                return Ok(new { message = "Tarefa associada com sucesso." });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao adicionar tarefa.");
                return StatusCode(500, ex.Message);
            }
        }

        [HttpGet("{userID}/tasks")]
        public ActionResult<List<Task>> GetTasks(int userID)
        {
            try
            {
                var target = _taskdb.GetTasksByUser(userID);
                return target == null ? NotFound("O utilizador não possui tarefas") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter tarefas.");
                return StatusCode(500, "Erro interno.");
            }
        }

        [HttpDelete("{userID}/task/{taskID}")]
        public IActionResult DeleteTask(int userID, int taskID)
        {
            try
            {
                _taskdb.RemoveTaskFromUser(userID, taskID);
                return Ok(new { message = "Tarefa removida com sucesso." });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar tarefa.");
                return StatusCode(500, "Erro interno.");
            }
        }

        // --- DTOs ---

        public class UserDTO
        {
            public string Name { get; set; } = "";
            public string Email { get; set; } = "";
            public string Password { get; set; } = "";
            public string Role { get; set; } = "user";
            public int CompanyID { get; set; }
        }
    }
}