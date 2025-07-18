﻿using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly DatabaseOperations _db;
        private readonly ILogger<UserController> _logger;

        public UserController(ILogger<UserController> logger, DatabaseOperations db)
        {
            _logger = logger;
            _db = db;
        }

        
        [Authorize]
        [HttpPost]
        public IActionResult Post([FromBody] UserDto user)
        {
            _db.CreateUser(user.Name, user.Email, user.Password, user.Role);
            return Ok(new { message = "Utilizador criado com sucesso." });
        }

       
        [HttpGet]
        public ActionResult<IEnumerable<User>> Get()
        {
            var reply = _db.GetUsers();
            return Ok(reply);
        }

       
        [HttpGet("{id}")]
        public ActionResult<User> Get(int id)
        {
            var user = _db.GetUser(id);
            if (user == null)
                return NotFound();

            return Ok(user);
        }

        
        [Authorize]
        [HttpPut("{id}")]
        public IActionResult Put(int id, [FromBody] UserDto user)
        {
            _db.EditUser(id, user.Name, user.Email, user.Password, user.Role);
            return Ok(new { message = "Utilizador atualizado com sucesso." });
        }

        
        [Authorize]
        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            _db.DeleteUser(id);
            return Ok(new { message = "Utilizador eliminado com sucesso." });
        }
    }

    public class UserDto
    {
        public string Name { get; set; } = "";
        public string Email { get; set; } = "";
        public string Password { get; set; } = "";
        public string Role { get; set; } = "";
    }
}
