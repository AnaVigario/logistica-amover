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

        
        [Authorize]
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
                    role = user.Role
                };
                _db.CreateUser(u);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao criar utilizador.");
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "Utilizador criado com sucesso." });
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
            catch (Exception ex) {
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
                return target == null ? NotFound("Nenhum utilizador encontrado com o userID especificado") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter utilizador com ID {userID}", userID);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        
        [Authorize]
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
                    role = user.Role
                };
                return _db.EditUser(u) ? Ok(new { message = "Utilizador atualizado com sucesso." }) : NotFound("Utilizador não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao atualizar utilizador com ID {userID}", userID);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        
        [Authorize]
        [HttpDelete("{userID}")]
        public IActionResult Delete(int userID)
        {
            try
            { 
                return _db.DeleteUser(userID) ? Ok(new { message = "Utilizador eliminado com sucesso." }) : NotFound("Utilizador não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar utilizador com ID {userID}", userID);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        [HttpPost("{userID}/tasks")]
        public IActionResult PostTask(int userID, [FromBody] int taskID)
        {
            try
            {
                _taskdb.AddTaskToUser(userID, taskID);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao adicionar tarefa a utilizador.");
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "Tarefa associada com sucesso." });
        }


        [HttpGet("{userID}/tasks")]
        public ActionResult<List<Task>> GetTasks(int userID)
        {
            try
            {
                var target = _taskdb.GetTasksByUser(userID);
                return target == null ? NotFound("O utilizador não possui tarefas associadas") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter tarefas do utilizador com ID {userID}", userID);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }


        [Authorize]
        [HttpDelete("{userID}/tasks")]
        public IActionResult DeleteTask(int userID, int taskID)
        {
            try
            {
                _taskdb.RemoveTaskFromUser(userID, taskID);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar utilizador com ID {id}", userID);
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "Tarefa removida com sucesso." });
        }
    }

    public class UserDTO
    {
        public string Name { get; set; } = "";
        public string Email { get; set; } = "";
        public string Password { get; set; } = "";
        public string Role { get; set; } = "";
    }
}
