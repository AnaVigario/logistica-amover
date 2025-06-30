using System.ComponentModel.DataAnnotations;

namespace projeto.Data.Models
{
    public class Vehicle
    {
        [Key]
        public int ID { get; set; }
        [Required]
        public string VID { get; set; } // Vehicle Identification Number
        
        //FK
        public int ownerID { get; set; } // User n - 1
        
        //NAV
        public User owner { get; set; } // User n - 1 navigation
    }
}
