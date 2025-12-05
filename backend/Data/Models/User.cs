using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class User
    {
        [Key]
        public int ID { get; set; }
        [Required]
        public string name { get; set; }
        [Required]
        public string email { get; set; }
        [Required]
        public string password { get; set; }
        [Required]
        public string role { get; set; }

        //FK

        public int? companyID { get; set; }

        //NAV
        public virtual List<Task>? tasks { get; set; }
        public virtual List<Plan>? plans { get; set; } // 1 - n
        public virtual Company company { get; set; }
        public virtual List<Vehicle> vehicles { get; set; } // 1 - n
        public virtual List<Alert> targetedAlerts { get; set; } // 1 - n
        public virtual List<Alert> managedAlerts { get; set; } // 1 - n
    }
}
