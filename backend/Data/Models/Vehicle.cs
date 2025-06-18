namespace projeto.Data.Models
{
    public class Vehicle
    {
        public int ID { get; set; }
        public string VID { get; set; } // Vehicle Identification Number
        public User Owner { get; set; }
    }
}
