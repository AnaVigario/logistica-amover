using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Servico
    {
        [Key]
        public int id_servico { get; set; }

        [Required]
        public string tipo { get; set; }

        [Required]
        public DateTime data { get; set; }


        [Required]
        public string descricao { get; set; }

        [Required]
        public string estado { get; set; }

        public virtual Utilizadores utilizador { get; set; }
        public virtual List<Tarefa> tarefas { get; set; }
    }
}
