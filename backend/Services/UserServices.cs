using projeto.Data;
using projeto.Data.Models;

namespace projeto.Services
{
    public class UserServices
    {
        private readonly AMoverContext _context;
        public UserServices(AMoverContext context)
        {
            _context = context;
        }

        public void CreateUser(User u)
        {
            try
            {
                if (_context.users.Any(x => x.email == u.email))
                {
                    throw new Exception("Já existe um utilizador com este email.");
                }
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao verificar utilizador: " + ex.Message);
            }
            _context.users.Add(u);
            _context.SaveChanges();
        }

        public List<User> GetUsers()
        {
            try
            {
                return _context.users.ToList();
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter lista de utilizadores: " + ex.Message);
            }
        }

        public User GetUserByID(int id)
        {
            try
            {
                var target = _context.users.Where(x => x.ID == id).FirstOrDefault();
                if (target == null)
                {
                    throw new Exception("Utilizador não encontrado.");
                }
                return target;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao obter o utilizador: " + ex.Message);
            }
        }

        public bool EditUser(User u)
        {
            try
            {
                var target = _context.users.Where(x => x.ID == u.ID).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                target.name = u.name;
                target.email = u.email;
                target.password = u.password;
                target.role = u.role;
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao editar o utilizador: " + ex.Message);
            }
        }

        public bool DeleteUser(int userId)
        {
            try
            {
                var target = _context.users.Where(x => x.ID == userId).FirstOrDefault();
                if (target == null)
                {
                    return false;
                }
                _context.users.Remove(target);
                _context.SaveChanges();
                return true;
            }
            catch (Exception ex)
            {
                throw new Exception("Erro ao eliminar o utilizador: " + ex.Message);
            }
        }
    }
}
