using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Client
    {
        [Key]
        public int ID { get; set; }
        [Required]
        public string name { get; set; }
        [Required]
        public string nif { get; set; }
        [Required]
        public string address { get; set; }
        [Required]
        public string phone { get; set; }
        [Required]
        public string email { get; set; }

        //NAV

        public virtual List<Task>? tasks { get; set; }
    }
}
