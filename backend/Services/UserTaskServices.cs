using projeto.Data;
using projeto.Data.Models;
using System.Threading.Tasks;
using Task = projeto.Data.Models.Task;

namespace projeto.Services
{
    public class UserTaskServices
    {
        private readonly AMoverContext _context;
        public UserTaskServices(AMoverContext context)
        {
            _context = context;
        }

        public void AddTaskToUser(int userID, int taskID)
        {
            try
            {
                var targetUser = _context.users.Find(userID);
                if (targetUser == null)
                    throw new Exception("Usuário não encontrado.");
                var targetTask = _context.tasks.Find(taskID);
                if (targetTask == null)
                    throw new Exception("Tarefa não encontrada.");
                targetTask.user = targetUser;
                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao adicionar tarefa ao usuário: " + ex.Message);
            }
        }

        public void RemoveTaskFromUser(int userID, int taskID)
        {
            try
            {
                var targetUser = _context.users.Find(userID);
                if (targetUser == null)
                    throw new Exception("Usuário não encontrado.");
                var targetTask = _context.tasks.Find(taskID);
                if (targetTask == null)
                    throw new Exception("Tarefa não encontrada.");
                if (targetTask.userID != userID)
                    throw new Exception("A tarefa não está atribuída a este usuário.");
                targetTask.userID = null;
                _context.SaveChanges();

            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao remover tarefa do usuário: " + ex.Message);
            }
        }

        public List<Task> GetTasksByUser(int userID)
        {
            try
            {
                var targetUser = _context.users.Find(userID);
                if (targetUser == null)
                    throw new Exception("Usuário não encontrado.");
                var tasks = _context.tasks.Where(t => t.userID == userID).ToList();
                return tasks;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter tarefas do usuário: " + ex.Message);
            }
        }
    }
}
