using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema; // Adicionado para [ForeignKey]

namespace projeto.Data.Models
{
    public enum RecurrenceType
    {
        None,
        Daily,
        Weekly
    }

    public class Task
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string type { get; set; }

        [Required]
        public DateTime creationDate { get; set; } = DateTime.Now;

        public DateTime? deadline { get; set; }

        public TimeSpan? availableTimeStart { get; set; }
        public TimeSpan? availableTimeEnds { get; set; }
        public RecurrenceType recurrence { get; set; }

        [Required]
        public string description { get; set; }

        [Required]
        public string status { get; set; } = "Unassigned";

        // FKs
        public int? userID { get; set; }
        public int? planID { get; set; }

        [Required]
        public int serviceID { get; set; }

        [Required]
        public int clientID { get; set; }

    

        [ForeignKey("userID")]
        public virtual User? user { get; set; }

        [ForeignKey("planID")]
        public virtual Plan? plan { get; set; }

        [ForeignKey("serviceID")]
        public virtual Service? service { get; set; }

        [ForeignKey("clientID")]
        public virtual Client? client { get; set; }

        public virtual List<LocationNode>? Nodes { get; set; }
    }
}