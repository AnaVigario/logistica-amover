using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Alert
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string description { get; set; }

        [Required]
        public DateTime timestamp { get; set; }
        
        //FK
        public int adminID { get; set; } // User n - 1

        //NAV
        public virtual User admin { get; set; }
        public virtual List<User> targets { get; set; }
    }
}
