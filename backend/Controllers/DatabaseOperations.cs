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
