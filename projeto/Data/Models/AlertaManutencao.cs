using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class AlertaManutencao
    {
        [Key]
        public int id_alerta { get; set; }


        [Required]
        public string descricao { get; set; }

        [Required]
        public DateTime data { get; set; }

        public virtual Utilizadores admin { get; set; }
        public virtual List<Utilizadores> Utilizadores { get; set; }


    }
}
