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
            throw new NotImplementedException();
        }

        public void RemoveTaskFromUser(int userID, int taskID)
        {
            throw new NotImplementedException();
        }

        public List<Task> GetTasksByUser(int userID)
        {
            throw new NotImplementedException();
        }
    }
}
