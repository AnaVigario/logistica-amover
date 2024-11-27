using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Tarefa
    {
        [Key]
        public int id_tarefa { get; set; }

        [Required]
        public string tipo { get; set; }

        [Required]
        public DateTime data { get; set; }

        public DateTime DeadLine { get; set; }

        [Required]
        public string descricao { get; set; }

        [Required]
        public string estado { get; set; }

        public List<string> coordenadas { get; set; }

        public virtual List<Tarefa> tarefas { get; set; } 
        public virtual Utilizadores utilizador { get; set; }
        public virtual Servico servico { get; set; }


    }
}
