using projeto.Data;
using projeto.Data.Models;
using Task = projeto.Data.Models.Task;

namespace projeto.Services
{
    public class TaskServices
    {
        private readonly AMoverContext _context;
        public TaskServices(AMoverContext context)
        {
            _context = context;
        }

        public void CreateTask(Task t, int sID, int cID)
        {
            try
            {
                t.creationDate = DateTime.Now;
                var service = _context.services.Find(sID);
                if (service == null)
                    throw new Exception("Serviço não encontrado.");
                t.service = service;
                var client = _context.clients.Find(cID);
                if (client == null)
                    throw new Exception("Cliente não encontrado.");
                t.client = client;
                _context.tasks.Add(t);
                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao criar tarefa: " + ex.Message);
            }
        }

        public List<Task> GetTasks()
        {
            throw new NotImplementedException();
        }

        public Task GetTaskByID(int id)
        {
            throw new NotImplementedException();
        }

        public bool DeleteTask(int id)
        {
            throw new NotImplementedException();
        }

        public bool AddTaskNode(int taskID, int nodeID)
        {
            throw new NotImplementedException();
        }

        public bool RemoveTaskNode(int taskID, int nodeID)
        {
            throw new NotImplementedException();
        }
    }
}
