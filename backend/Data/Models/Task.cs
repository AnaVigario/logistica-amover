using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Task
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string type { get; set; }

        [Required]
        public DateTime creation_date { get; set; }

        public DateTime deadline { get; set; }

        [Required]
        public string description { get; set; }

        [Required]
        public string status { get; set; }

        public List<string> coordinates { get; set; }

        public virtual List<Task> tasks { get; set; } 
        public virtual User users { get; set; }
        public virtual Service service { get; set; }


    }
}
