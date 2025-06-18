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
        public IActionResult Post([FromBody] VehicleDTO v)
        {
            // CreateVehicle()
            try
            {
                // db.CreateVehicle(v.VID);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao criar veículo.");
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "temp" });
        }


        [HttpGet]
        public ActionResult<IEnumerable<Vehicle>> Get()
        {
            try
            {
                // GetVehicles()
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter veículos.");
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(/* reply */);
        }


        [HttpGet("{VID}")]
        public ActionResult<User> Get(int VID)
        {
            try
            {
                // GetVehicle(VID)
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao obter veículo com VID {VID}", VID);
                return StatusCode(500, "Erro interno do servidor.");
            }

            return Ok(/* vehicle */);
        }


        [Authorize]
        [HttpPut("{id}")]
        public IActionResult Put(int id, [FromBody] VehicleDTO vehicle)
        {
            try
            {
                // Check if vehicle exists
                // If not, return NotFound()
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao atualizar veículo com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "Veículo atualizado com sucesso." });
        }


        [Authorize]
        [HttpDelete("{id}")]
        public IActionResult Delete(int id)
        {
            try
            {
                // DeleteVehicle(id)
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Erro ao eliminar veículo com ID {id}", id);
                return StatusCode(500, "Erro interno do servidor.");
            }
            return Ok(new { message = "temp" });
        }
    }

    public class VehicleDTO
    {
        public string VID { get; set; } = ""; 
    }
}
