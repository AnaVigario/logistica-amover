using projeto.Data;
using projeto.Data.Models;

namespace projeto.Services
{
    public class VehicleServices
    {
        private readonly AMoverContext _context;
        public VehicleServices(AMoverContext context)
        {
            _context = context;
        }

        public void CreateVehicle(Vehicle v)
        {
            try
            {
                if (_context.vehicles.Any(x => x.VID == v.VID))
                {
                    throw new Exception("Veículo com o mesmo VID já existe.");
                }
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao verificar veículo: " + ex.Message);
            }
            _context.vehicles.Add(v);
            _context.SaveChanges();
        }

        public List<Vehicle> GetVehicles()
        {
            try
            {
                return _context.vehicles.ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter veículos: " + ex.Message);
            }
        }


        public Vehicle GetVehicleByID(string VID)
        {
            try
            {
                var target = _context.vehicles.Where(x => x.VID == VID).FirstOrDefault();
                if (target == null)
                {
                    throw new Exception("Veículo não encontrado.");
                }
                return target;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter veículo: " + ex.Message);
            }
        }

        public bool EditVehicle(Vehicle v)
        {
            try
            {
                var target = _context.vehicles.Where(x => x.ID == v.ID).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                target.VID = v.VID;
                target.batteryCapacity = v.batteryCapacity;
                target.cargoCapacity = v.cargoCapacity;
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao editar veículo: " + ex.Message);
            }
        }

        public bool DeleteVehicle(int id)
        {
            try
            {
                var target = _context.vehicles.Where(x => x.ID == id).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                _context.vehicles.Remove(target);
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao excluir veículo: " + ex.Message);
            }
        }
    }
}
