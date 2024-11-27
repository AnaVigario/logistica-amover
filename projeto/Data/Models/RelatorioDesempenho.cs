using System.ComponentModel.DataAnnotations;
namespace projeto.Data.Models
{
    public class RelatorioDesempenho
    {
        [Key]
        public int id_relatorio { get; set; }


        [Required]
        public string descricao { get; set; }

        [Required]
        public DateTime data { get; set; }

    }
}

