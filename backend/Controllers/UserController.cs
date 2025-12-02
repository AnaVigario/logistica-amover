using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;
using projeto.Services;

namespace projeto.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly UserServices _db;
        private readonly ILogger<UserController> _logger;

        public UserController(ILogger<UserController> logger, UserServices db)
        {
            _logger = logger;
            _db = db;
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

       
        [HttpGet("{id}")]
        public ActionResult<User> Get(int id)
        {
            try
            {
                var target = _db.GetUserByID(id);
                return target == null ? NotFound("Nenhum utilizador encontrado com o ID especificado") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter utilizador com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        
        [Authorize]
        [HttpPut("{id}")]
        public IActionResult Put(int id, [FromBody] UserDTO user)
        {
            try
            {
                User u = new User
                {
                    ID = id,
                    name = user.Name,
                    email = user.Email,
                    password = user.Password,
                    role = user.Role
                };
                return _db.EditUser(u) ? Ok(new { message = "Utilizador atualizado com sucesso." }) : NotFound("Utilizador não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao atualizar utilizador com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }

        
        [Authorize]
        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            try
            { 
                return _db.DeleteUser(id) ? Ok(new { message = "Utilizador eliminado com sucesso." }) : NotFound("Utilizador não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar utilizador com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
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
