using Microsoft.EntityFrameworkCore;
using projeto.Data;
using projeto.Data.Models;

namespace projeto.Controllers
{
    public class DatabaseOperations
    {
        private readonly AMoverContext db;

        public DatabaseOperations(AMoverContext context)
        {
            db = context;
        }

        public void CreateUser(string name, string email, string password, string type)
        {
            User user = new User();
            user.type = type;
            user.email = email;
            user.name = name;
            user.password = password;
            db.users.Add(user);
            db.SaveChanges();
        }

        public List<User> GetUsers()
        {
            return db.users.ToList();
        }

        public User GetUser(int id)
        {
            return db.users.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditUser(int id, string nome, string email, string senha, string tipo_utilizador)
        {
            User user = db.users.Where(x => x.ID == id).FirstOrDefault();
            user.type = tipo_utilizador;
            user.email = email;
            user.name = nome;
            user.password = senha;
            db.SaveChanges();
        }

        public void DeleteUser(int userId)
        {
            User user = db.users.Where(x => x.ID == userId).FirstOrDefault();
            db.users.Remove(user);
            db.SaveChanges();
        }

        public int Login(string email, string password)
        {
            User user = db.users.Where(x => x.email == email).FirstOrDefault();
            if (user != null && user.password == password)
            {
                return user.ID;
            }
            return -1;
        }

        public void CreateAlert(Alert alert, List<int> _targets)
        {
            try
            {
                alert.targets = new List<User>();
                alert.timestamp = DateTime.UtcNow;
                foreach (int id1 in _targets)
                {
                    if (!db.users.Any(x => x.ID == id1))
                    {
                        throw new Exception("Usuário com ID " + id1 + " não encontrado.");
                    } else {
                        alert.targets.Add(db.users.Where(x => x.ID == id1).FirstOrDefault());
                    }
                }
                db.alerts.Add(alert);
                db.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao criar alerta: " + ex.Message);
            }
        }

        public List<Alert> GetAlerts()
        {
            List<Alert> alerts = db.alerts.Include(y => y.targets).ToList();
            if (alerts != null)
            {
                return alerts;
            }
            return null;
        }

        public Alert GetAlert(int id)
        {
            return db.alerts.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditAlert(int id, string description, int adminId, List<int> targetIds)
        {
            Alert alert = db.alerts.Where(x => x.ID == id).FirstOrDefault();
            alert.description = description;
            alert.timestamp = DateTime.Now;
            alert.adminID = adminId;
            alert.targets = new List<User>();
            foreach (int id1 in targetIds)
            {
                alert.targets.Add(db.users.Where(x => x.ID == id1).FirstOrDefault());
            }
            db.SaveChanges();
        }

        public void DeleteAlert(int alertId)
        {
            Alert alert = db.alerts.Where(x => x.ID == alertId).FirstOrDefault();
            db.alerts.Remove(alert);
            db.SaveChanges();
        }
        /*
        public void CreateTask(string type, DateTime deadline, string description, string status, int userId, int serviceId, List<string> coordinates)
        {
            Data.Models.Task task = new Data.Models.Task();
            task.type = type;
            task.deadline = deadline;
            task.deadline = DateTime.Now;
            task.description = description;
            task.status = status;
            task.users = db.users.Where(x => x.ID == userId).FirstOrDefault();
            task.service = db.services.Where(x => x.ID == serviceId).FirstOrDefault();
            task.coordinates = coordinates;
            db.tasks.Add(task);
            db.SaveChanges();
        }

        public List<Data.Models.Task> GetTasks()
        {
            return db.tasks.ToList();
        }

        public Data.Models.Task GetTask(int id)
        {
            return db.tasks.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditTask(int id, string type, DateTime deadline, string description, string status, int userId, int serviceId, List<string> coordinates)
        {
            Data.Models.Task task = db.tasks.Where(x => x.ID == id).FirstOrDefault();
            task.type = type;
            task.deadline = deadline;
            task.description = description;
            task.status = status;
            task.users = db.users.Where(x => x.ID == userId).FirstOrDefault();
            task.service = db.services.Where(x => x.ID == serviceId).FirstOrDefault();
            task.coordinates = coordinates;
            db.SaveChanges();
        }

        public void DeleteTask(int taskId)
        {
            Data.Models.Task task = db.tasks.Where(x => x.ID == taskId).FirstOrDefault();
            db.tasks.Remove(task);
            db.SaveChanges();
        }

        public void CreateService(string type, string description, string status, int userId)
        {
            Service service = new Service();
            service.type = type;
            service.date = DateTime.Now;
            service.description = description;
            service.status = status;
            service.user = db.users.Where(x => x.ID == userId).FirstOrDefault();
            db.services.Add(service);
            db.SaveChanges();
        }

        public List<Service> GetServices()
        {           
            return db.services.ToList();
        }

        public Service GetService(int id)
        {
            return db.services.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditService(int id, string type, string description, string status, int userId)
        {
            Service service = db.services.Where(x => x.ID == id).FirstOrDefault();
            service.type = type;
            service.description = description;
            service.status = status;
            service.user = db.users.Where(x => x.ID == userId).FirstOrDefault();
            db.SaveChanges();
        }

        public void DeleteService(int serviceId)
        {
            Service service = db.services.Where(x => x.ID == serviceId).FirstOrDefault();
            db.services.Remove(service);
            db.SaveChanges();
        }
        */
        public void CreateVehicle(Vehicle v)
        {
            try
            {
                if (db.vehicles.Any(x => x.VID == v.VID))
                {
                    throw new Exception("Veículo com o mesmo VID já existe.");
                }
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao verificar veículo: " + ex.Message);
            }
            db.vehicles.Add(v);
            db.SaveChanges();
        }

        public Vehicle GetVehicle(string VID)
        {
            try
            {
                var target = db.vehicles.Where(x => x.VID == VID).FirstOrDefault();
                return target;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter veículo: " + ex.Message);
            }
        }

        public List<Vehicle> GetVehicles()
        {
            try
            {
                return db.vehicles.ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter veículos: " + ex.Message);
            }
        }

        public bool EditVehicle(Vehicle v)
        {
            try
            {
                var target = db.vehicles.Where(x => x.ID == v.ID).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                target.VID = v.VID;
                db.SaveChanges();
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
                var target = db.vehicles.Where(x => x.ID == id).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                db.vehicles.Remove(target);
                db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao excluir veículo: " + ex.Message);
            }
        }

        public bool CreateRoute(string description)
        {
            try
            {
                Data.Models.Route route = new Data.Models.Route();
                route.description = description;
                db.routes.Add(route);
                db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao criar rota: " + ex.Message);
            }
        }

        public bool CreateNode(float lat, float lon, string description)
        {
            try
            {
                LocationNode node = new LocationNode();
                node.latitude = lat;
                node.longintude = lon;
                node.description = description;
                db.locationNodes.Add(node);
                db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao criar nó: " + ex.Message);
            }
        }

        public bool AddNodeToRoute(int routeId, int nodeId)
        {
            try
            {
                var route = db.routes.Include(r => r.nodes).FirstOrDefault(r => r.ID == routeId);
                if (route == null)
                {
                    throw new Exception("Rota não encontrada.");
                }
                var node = db.locationNodes.FirstOrDefault(n => n.ID == nodeId);
                if (node == null)
                {
                    throw new Exception("Nó não encontrado.");
                }
                if (route.nodes.Any(n => n.ID == nodeId))
                {
                    throw new Exception("Nó já está associado a esta rota.");
                }
                route.nodes.Add(node);
                db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao adicionar nó à rota: " + ex.Message);
            }
        }

        public bool RemoveNodeFromRoute(int routeId, int nodeId)
        {
            try
            {
                var route = db.routes.Include(r => r.nodes).FirstOrDefault(r => r.ID == routeId);
                if (route == null)
                {
                    throw new Exception("Rota não encontrada.");
                }
                var node = route.nodes.FirstOrDefault(n => n.ID == nodeId);
                if (node == null)
                {
                    throw new Exception("Nó não encontrado na rota.");
                }
                route.nodes.Remove(node);
                db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao remover nó da rota: " + ex.Message);
            }
        }

        public List<LocationNode> GetNodes()
        {
            try
            {
                return db.locationNodes.ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter nós: " + ex.Message);
            }
        }

        public List<Data.Models.Route> GetRoutes()
        {
            try
            {
                return db.routes.Include(r => r.nodes).ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter rotas: " + ex.Message);
            }
        }

        public Data.Models.Route GetRoute(int id)
        {
            try
            {
                var r = db.routes.Include(r => r.nodes).Where(r => r.ID == id).FirstOrDefault();
                return (r == null ? throw new Exception("Rota não encontrada.") : r);
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter rota: " + ex.Message);
            }
        }

        public bool DeleteRoute(int routeID)
        {
            try
            {
                var route = db.routes.Include(r => r.nodes).FirstOrDefault(r => r.ID == routeID);
                if (route == null)
                {
                    throw new Exception("Rota não encontrada.");
                }
                db.routes.Remove(route);
                db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao excluir rota: " + ex.Message);
            }
        }

        /*
        public void CreateReport(string description)
        {
            PerformanceReport relatorioDesempenho = new PerformanceReport();
            relatorioDesempenho.description = description;
            relatorioDesempenho.timestamp = DateTime.Now;
            db.reports.Add(relatorioDesempenho);
            db.SaveChanges();
        }

        public List<PerformanceReport> GetReports()
        {
            return db.reports.ToList();
        }

        */
        public ApiKey GetApiKey(string key)
        {
            return db.apiKeys.FirstOrDefault(k => k.Key == key && k.IsActive);
        }

        public void CreateMessageLog(MessageLog log)
        {
            db.messageLogs.Add(log);
            db.SaveChanges();
        }
    }
}
