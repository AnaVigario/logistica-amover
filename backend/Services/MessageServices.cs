using projeto.Data;
using projeto.Data.Models;

namespace projeto.Services
{
    public class MessageServices
    {
        private readonly AMoverContext _context;
        public MessageServices(AMoverContext context)
        {
            _context = context;
        }

        public ApiKey GetApiKey(string key)
        {
            return _context.apiKeys.FirstOrDefault(k => k.Key == key && k.IsActive);
        }

        public void CreateMessageLog(MessageLog log)
        {
            _context.messageLogs.Add(log);
            _context.SaveChanges();
        }
    }
}
