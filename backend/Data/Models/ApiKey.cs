using System.ComponentModel.DataAnnotations;

namespace projeto.Data.Models
{
    public class ApiKey
    {
        [Key]
        public int ID { get; set; }
        public string Key { get; set; }
        public string Owner { get; set; } // Nome da aplicação, ex: "Logistica"
        public bool IsActive { get; set; } = true;
    }
}
