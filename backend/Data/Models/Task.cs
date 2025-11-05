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
        public DateTime creationDate { get; set; }

        public DateTime deadline { get; set; }

        [Required]
        public string description { get; set; }

        [Required]
        public string status { get; set; }

        //FK
        public int? routeID { get; set; } // Route n - 1
        public int? userID { get; set; } // User n - 1
        public int? parentTaskID { get; set; } // Parent Task n - 1
        public int serviceID { get; set; } // Service n - 1
        public int clientID { get; set; } // Client n - 1
        //NAV
        public virtual Task parentTask { get; set; } // Parent Task 1 - n
        public virtual List<Task> subTasks { get; set; } // n - n

        public virtual User user { get; set; }
        public virtual Service service { get; set; }
        public virtual Client client { get; set; }
        public virtual Route route { get; set; }

    }
}
