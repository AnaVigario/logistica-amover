using System.ComponentModel.DataAnnotations;

namespace projeto.Data.Models
{
    public class MessageLog
    {
        [Key]
        public int ID { get; set; }
        public string VehicleVID { get; set; }
        public string Sender { get; set; }
        public string Content { get; set; }
        public DateTime Timestamp { get; set; }
        public string Status { get; set; } // ex: "processed", "invalid_vin"
    }
}
