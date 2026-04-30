using Microsoft.EntityFrameworkCore;
using projeto.Data;
using projeto.Data.Models;
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
                var targetTask = _context.tasks.Find(taskID);

                if (targetUser == null) throw new Exception("Utilizador não encontrado.");
                if (targetTask == null) throw new Exception("Tarefa não encontrada.");

                // Associamos o ID e aproveitamos para atualizar o status
                targetTask.userID = userID;
                targetTask.status = "Assigned"; // Fica melhor para mostrar no vídeo

                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao associar tarefa: " + ex.Message);
            }
        }

        public void RemoveTaskFromUser(int userID, int taskID)
        {
            try
            {
                var targetTask = _context.tasks.FirstOrDefault(t => t.ID == taskID && t.userID == userID);

                if (targetTask == null)
                    throw new Exception("Tarefa não encontrada ou não pertence a este utilizador.");

                targetTask.userID = null;
                targetTask.status = "Unassigned"; // Volta ao estado inicial

                _context.SaveChanges();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao remover tarefa: " + ex.Message);
            }
        }

        public List<Task> GetTasksByUser(int userID)
        {
            try
            {
                // Usamos o Include para que o vídeo mostre o nome do cliente e serviço
                return _context.tasks
                    .Include(t => t.service)
                    .Include(t => t.client)
                    .Where(t => t.userID == userID)
                    .ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter tarefas: " + ex.Message);
            }
        }
    }
}