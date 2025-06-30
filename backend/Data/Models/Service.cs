using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Service
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string type { get; set; }

        [Required]
        public string description { get; set; }

        [Required]
        public DateTime date { get; set; }

        [Required]
        public string status { get; set; }

        //FK

        //NAV

        public virtual User user { get; set; }
        public virtual List<Task> tasks { get; set; }
    }
}
