namespace projeto.Data.Models
{
    public class UserAlertas
    {
        public int id_user { get; set; }
        public int id_alerta { get; set; }
        public Utilizadores Utilizador { get; set; }
        public AlertaManutencao Alerta {  get; set; }
    }
}
