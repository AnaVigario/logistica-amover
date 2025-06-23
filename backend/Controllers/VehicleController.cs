using System.Net.NetworkInformation;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class VehicleController : ControllerBase
    {
        private static DatabaseOperations db = new DatabaseOperations();
        private readonly ILogger<VehicleController> _logger;

        public VehicleController(ILogger<VehicleController> logger)
        {
            _logger = logger;
        }


        [Authorize]
        [HttpPost]
        public IActionResult Post([FromBody] VehicleDTO _v)
        {
            try
            {
                Vehicle v = new Vehicle
                {
                    VID = _v.VID
                };
                db.CreateVehicle(v);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao criar veículo.");
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "Veículo registado com sucesso" });
        }


        [HttpGet]
        public ActionResult<IEnumerable<Vehicle>> Get()
        {
            try
            {
                List<Vehicle> targets = db.GetVehicles();
                if (targets == null || targets.Count == 0)
                {
                    return NotFound("Nenhum veículo encontrado.");
                }
                return Ok(targets);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter veículos.");
                return StatusCode(500, "Erro interno do servidor.");
            }
        }


        [HttpGet("{VID}")]
        public ActionResult<User> Get(int VID)
        {
            try
            {
                var target = db.GetVehicle(VID.ToString());
                return target == null ? NotFound("Nenhum veículo encontrado com o VID expecificado") : Ok(target);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter veículo com VID {VID}", VID);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }


        [Authorize]
        [HttpPut("{id}")]
        public IActionResult Put(int id, [FromBody] VehicleDTO vehicle)
        {
            try
            {
                Vehicle v = new Vehicle
                {
                    ID = id,
                    VID = vehicle.VID
                };
                return db.EditVehicle(v) ? Ok(new { message = "Veículo atualizado com sucesso." }) : NotFound("Veículo não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao atualizar veículo com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }


        [Authorize]
        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            try
            {
                return db.DeleteVehicle(id) ? Ok(new { message = "Veículo eliminado com sucesso." }) : NotFound("Veículo não encontrado.");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar veículo com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
        }
    }

    public class VehicleDTO
    {
        public string VID { get; set; } = ""; 
    }
}
