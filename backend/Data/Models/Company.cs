using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Company
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string name { get; set; }

        [Required]
        public string description { get; set; }

        //FK

        

        //NAV
        public virtual List<User> users { get; set; }
        //public virtual List<Service> services { get; set; }
    }
}
