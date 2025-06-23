using projeto.Data;
using projeto.Data.Models;
using System.Data.Entity;

namespace projeto.Controllers
{
    public class DatabaseOperations
    {
        public static BackEndContext Db = new BackEndContext();

        public void CreateUser(string name, string email, string password, string type)
        {
            User user = new User();
            user.type = type;
            user.email = email;
            user.name = name;
            user.password = password;
            Db.users.Add(user);
            Db.SaveChanges();
        }

        public List<User> GetUsers()
        {
            return Db.users.ToList();
        }

        public User GetUser(int id)
        {
            return Db.users.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditUser(int id, string nome, string email, string senha, string tipo_utilizador)
        {
            User user = Db.users.Where(x => x.ID == id).FirstOrDefault();
            user.type = tipo_utilizador;
            user.email = email;
            user.name = nome;
            user.password = senha;
            Db.SaveChanges();
        }

        public void DeleteUser(int userId)
        {
            User user = Db.users.Where(x => x.ID == userId).FirstOrDefault();
            Db.users.Remove(user);
            Db.SaveChanges();
        }

        public int Login(string email, string password)
        {
            User user = Db.users.Where(x => x.email == email).FirstOrDefault();
            if (user != null && user.password == password)
            {
                return user.ID;
            }
            return -1;
        }

        public void CreateAlert(string description, int adminId, List<int> targetIds)
        {
            Alert alert = new Alert();
            alert.description = description;
            alert.timestamp = DateTime.Now;
            alert.linked_admin = Db.users.Where(x => x.ID == adminId).FirstOrDefault();
            alert.targets = new List<User>();
            foreach (int id1 in targetIds) 
            {
                alert.targets.Add(Db.users.Where(x => x.ID == id1).FirstOrDefault());
            }
            Db.alerts.Add(alert);
            Db.SaveChanges();
        }

        public List<Alert> GetAlerts()
        {
            List<Alert> alerts = Db.alerts.Include(y => y.targets).ToList();
            if (alerts != null)
            {
                return alerts;
            }
            return null;
        }

        public Alert GetAlert(int id)
        {
            return Db.alerts.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditAlert(int id, string description, int adminId, List<int> targetIds)
        {
            Alert alert = Db.alerts.Where(x => x.ID == id).FirstOrDefault();
            alert.description = description;
            alert.timestamp = DateTime.Now;
            alert.linked_admin = Db.users.Where(x => x.ID == adminId).FirstOrDefault();
            alert.targets = new List<User>();
            foreach (int id1 in targetIds)
            {
                alert.targets.Add(Db.users.Where(x => x.ID == id1).FirstOrDefault());
            }
            Db.SaveChanges();
        }

        public void DeleteAlert(int alertId)
        {
            Alert alert = Db.alerts.Where(x => x.ID == alertId).FirstOrDefault();
            Db.alerts.Remove(alert);
            Db.SaveChanges();
        }

        public void CreateTask(string type, DateTime deadline, string description, string status, int userId, int serviceId, List<string> coordinates)
        {
            Data.Models.Task task = new Data.Models.Task();
            task.type = type;
            task.deadline = deadline;
            task.deadline = DateTime.Now;
            task.description = description;
            task.status = status;
            task.users = Db.users.Where(x => x.ID == userId).FirstOrDefault();
            task.service = Db.services.Where(x => x.ID == serviceId).FirstOrDefault();
            task.coordinates = coordinates;
            Db.tasks.Add(task);
            Db.SaveChanges();
        }

        public List<Data.Models.Task> GetTasks()
        {
            return Db.tasks.ToList();
        }

        public Data.Models.Task GetTask(int id)
        {
            return Db.tasks.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditTask(int id, string type, DateTime deadline, string description, string status, int userId, int serviceId, List<string> coordinates)
        {
            Data.Models.Task task = Db.tasks.Where(x => x.ID == id).FirstOrDefault();
            task.type = type;
            task.deadline = deadline;
            task.description = description;
            task.status = status;
            task.users = Db.users.Where(x => x.ID == userId).FirstOrDefault();
            task.service = Db.services.Where(x => x.ID == serviceId).FirstOrDefault();
            task.coordinates = coordinates;
            Db.SaveChanges();
        }

        public void DeleteTask(int taskId)
        {
            Data.Models.Task task = Db.tasks.Where(x => x.ID == taskId).FirstOrDefault();
            Db.tasks.Remove(task);
            Db.SaveChanges();
        }

        public void CreateService(string type, string description, string status, int userId)
        {
            Service service = new Service();
            service.type = type;
            service.date = DateTime.Now;
            service.description = description;
            service.status = status;
            service.user = Db.users.Where(x => x.ID == userId).FirstOrDefault();
            Db.services.Add(service);
            Db.SaveChanges();
        }

        public List<Service> GetServices()
        {           
            return Db.services.ToList();
        }

        public Service GetService(int id)
        {
            return Db.services.Where(x => x.ID == id).FirstOrDefault();
        }

        public void EditService(int id, string type, string description, string status, int userId)
        {
            Service service = Db.services.Where(x => x.ID == id).FirstOrDefault();
            service.type = type;
            service.description = description;
            service.status = status;
            service.user = Db.users.Where(x => x.ID == userId).FirstOrDefault();
            Db.SaveChanges();
        }

        public void DeleteService(int serviceId)
        {
            Service service = Db.services.Where(x => x.ID == serviceId).FirstOrDefault();
            Db.services.Remove(service);
            Db.SaveChanges();
        }

        public void CreateVehicle(Vehicle v)
        {
            try
            {
                if (Db.vehicles.Any(x => x.VID == v.VID))
                {
                    throw new Exception("Veículo com o mesmo VID já existe.");
                }
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao verificar veículo: " + ex.Message);
            }
            Db.vehicles.Add(v);
            Db.SaveChanges();
        }

        public Vehicle GetVehicle(string VID)
        {
            try
            {
                var target = Db.vehicles.Where(x => x.VID == VID).FirstOrDefault();
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
                return Db.vehicles.ToList();
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
                var target = Db.vehicles.Where(x => x.ID == v.ID).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                target.VID = v.VID;
                Db.SaveChanges();
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
                var target = Db.vehicles.Where(x => x.ID == id).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                Db.vehicles.Remove(target);
                Db.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao excluir veículo: " + ex.Message);
            }
        }   

        public void CreateReport(string description)
        {
            PerformanceReport relatorioDesempenho = new PerformanceReport();
            relatorioDesempenho.description = description;
            relatorioDesempenho.timestamp = DateTime.Now;
            Db.reports.Add(relatorioDesempenho);
            Db.SaveChanges();
        }

        public List<PerformanceReport> GetReports()
        {
            return Db.reports.ToList();
        }
    }
}
