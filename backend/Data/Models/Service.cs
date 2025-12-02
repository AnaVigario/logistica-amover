using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Service
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string category { get; set; }

        [Required]
        public string description { get; set; }

        //FK

        public int companyID { get; set; }

        //NAV
        public virtual Company company { get; set; }
        public virtual List<Task>? tasks { get; set; }
    }
}
