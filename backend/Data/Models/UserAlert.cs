namespace projeto.Data.Models
{
    public class UserAlert
    {
        public int ID_user { get; set; }
        public int ID_alert { get; set; }
        public User Users { get; set; }
        public Alert Alert {  get; set; }
    }
}
