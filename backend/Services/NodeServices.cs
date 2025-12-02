using projeto.Data;
using projeto.Data.Models;

namespace projeto.Services
{
    public class NodeServices
    {
        private readonly AMoverContext _context;
        public NodeServices(AMoverContext context)
        {
            _context = context;
        }

        public bool CreateNode(float lat, float lon, string address)
        {
            try
            {
                LocationNode node = new LocationNode();
                node.latitude = lat;
                node.longintude = lon;
                node.address = address;
                _context.locationNodes.Add(node);
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao criar nó: " + ex.Message);
            }
        }

        public List<LocationNode> GetNodes()
        {
            try
            {
                return _context.locationNodes.ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter nós: " + ex.Message);
            }
        }
    }
}
