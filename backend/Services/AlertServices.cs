using projeto.Data;
using projeto.Data.Models;

namespace projeto.Services
{
    public class AlertServices
    {
        private readonly AMoverContext _context;
        public AlertServices(AMoverContext context)
        {
            _context = context;
        }

        public void CreateAlert(Alert alert, List<int> _targets)
        {
            try
            {
                alert.targets = new List<User>();
                alert.timestamp = DateTime.UtcNow;
                foreach (int id1 in _targets)
                {
                    if (!_context.users.Any(x => x.ID == id1))
                    {
                        throw new Exception("Usuário com ID " + id1 + " não encontrado.");
                    }
                    else
                    {
                        alert.targets.Add(_context.users.Where(x => x.ID == id1).FirstOrDefault());
                    }
                }
                _context.alerts.Add(alert);
                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao criar alerta: " + ex.Message);
            }
        }

        public List<Alert> GetAlerts()
        {
            try
            {
                return _context.alerts.ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter alertas: " + ex.Message);
            }
        }

        public Alert GetAlertByID(int id)
        {
            try
            {
                return _context.alerts.FirstOrDefault(x => x.ID == id);
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter alerta: " + ex.Message);
            }
        }

        public void DeleteAlert(int alertId)
        {
            Alert alert = _context.alerts.Where(x => x.ID == alertId).FirstOrDefault();
            _context.alerts.Remove(alert);
            _context.SaveChanges();
        }
    }
}
