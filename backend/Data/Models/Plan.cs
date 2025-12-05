using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace projeto.Data.Models
{
    public class Plan
    {
        [Key]
        public int ID { get; set; }
        public bool isOptimized { get; set; } = false;

        //FK
        public int userID { get; set; } // User n - 1

        //NAV
        public virtual User user { get; set; }
        public virtual List<Task> tasks { get; set; }
        //public virtual List<Task> tasks { get; set; }

    }
}
