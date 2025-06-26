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
        public int admin_id { get; set; }


        public virtual User linked_admin { get; set; }
        public virtual List<User> targets { get; set; }
    }
}
