using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace projeto.Data.Models
{
    /*
    public class Route
    {
        [Key]
        public int ID { get; set; }
        public string description { get; set; }

        //NAV
        public virtual List<LocationNode> nodes { get; set; }

    }
    */
    public class LocationNode
    {
        [Key]
        public int ID { get; set; }
        [Required]
        public float latitude { get; set; }
        [Required]
        public float longintude { get; set; }
        [Required]
        public string address { get; set; }

        public TimeSpan? availableTimeStart { get; set; }
        public TimeSpan? availableTimeEnds { get; set; }

        //FK

        //NAV

        public virtual List<Task>? tasks { get; set; }

        //[JsonIgnore]
        //public virtual List<LocationNode> routes { get; set; }
    }
}
