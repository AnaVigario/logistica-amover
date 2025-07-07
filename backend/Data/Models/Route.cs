using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace projeto.Data.Models
{
    public class Route
    {
        [Key]
        public int ID { get; set; }
        public string description { get; set; }

        //NAV
        public virtual List<LocationNode> nodes { get; set; }

    }
    public class LocationNode
    {
        [Key]
        public int ID { get; set; }
        [Required]
        public float latitude { get; set; }
        [Required]
        public float longintude { get; set; }
        [Required]
        public string description { get; set; }
        //FK

        //NAV
        [JsonIgnore]
        public virtual List<Route> routes { get; set; }
    }
}
