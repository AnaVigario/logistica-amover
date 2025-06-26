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
        public string type { get; set; }

        //FK

        //NAV
        public virtual List<Vehicle> vehicles { get; set; }
    }
}
