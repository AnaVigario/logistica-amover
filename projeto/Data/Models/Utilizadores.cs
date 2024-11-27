using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class Utilizadores
    {
        [Key]
        public int id { get; set; }

        [Required]
        public string name { get; set; }

        [Required]
        public string email { get; set; }


        [Required]
        public string senha { get; set; }

        [Required]
        public string tipo_utilizador { get; set; }

        public virtual List<Tarefa> tarefas { get; set; }
        public virtual List<Servico> servicos { get; set; }

        public virtual List<AlertaManutencao> alertas { get; set; }
    }
}
