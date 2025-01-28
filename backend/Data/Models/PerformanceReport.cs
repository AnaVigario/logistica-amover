using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class PerformanceReport
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string description { get; set; }

        [Required]
        public DateTime timestamp { get; set; }

    }
}

