using Microsoft.AspNetCore.Mvc;
using projeto.Data.Models;

namespace projeto.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class RouteController : ControllerBase
    {
        private readonly DatabaseOperations _db;
        private readonly ILogger<RouteController> _logger;

        public RouteController(ILogger<RouteController> logger, DatabaseOperations db)
        {
            _logger = logger;
            _db = db;
        }

        [HttpPost(Name = "PostRoute")]
        public void Post(string description)
        {
            try
            {
                _db.CreateRoute(description);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating route");
                throw new Exception("Error creating route", ex);
            }
            return;
        }

        [HttpPost("node", Name = "PostRouteNode")]
        public void PostPoint([FromBody] NodeDTO _n)
        {
            try
            {
                _db.CreateNode(_n.latitude, _n.longintude, _n.description);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error adding point to route");
                throw new Exception("Error adding point to route", ex);
            }
        }

        [HttpPost("{routeId}/node", Name = "LinkRouteNode")]
        public void LinkRouteNode(int routeId, int nodeId)
        {
            try
            {
                _db.AddNodeToRoute(routeId, nodeId);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error linking route to node");
                throw new Exception("Error linking route to node", ex);
            }
        }

        [HttpDelete("{routeId}/node", Name = "UnlinkRouteNode")]
        public void UnlinkRouteNode(int routeId, int nodeId)
        {
            try
            {
                _db.RemoveNodeFromRoute(routeId, nodeId);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error removing route from node");
                throw new Exception("Error removing route from node", ex);
            }
        }


        [HttpGet(Name = "GetRoutes")]
        public IEnumerable<Data.Models.Route> Get()
        {
            try
            {
                List<Data.Models.Route> reply = _db.GetRoutes();
                return reply;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving routes");
                throw new Exception("Error retrieving routes", ex);
            }
        }

        [HttpGet("{id}", Name = "GetRoute")]
        public Data.Models.Route Get(int id)
        {
            try
            {
                Data.Models.Route reply = _db.GetRoute(id);
                return reply;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving route with ID {id}", id);
                throw new Exception($"Error retrieving route with ID {id}", ex);
            }
        }

        [HttpGet("node", Name = "GetAllNodes")]
        public List<LocationNode> GetAllNodes()
        {
            try
            {
                List<LocationNode> reply = _db.GetNodes();
                return reply;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error retrieving all nodes");
                throw new Exception("Error retrieving all nodes", ex);
            }
        }

        [HttpDelete("{id}", Name = "DeleteRoute")]
        public void Delete(int id)
        {
            try
            {
                _db.DeleteRoute(id);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting route with ID {id}", id);
                throw new Exception($"Error deleting route with ID {id}", ex);
            }
        }
    }
    public class NodeDTO
    {
        public float latitude { get; set; }
        public float longintude { get; set; }
        public string description { get; set; }
    }
}
