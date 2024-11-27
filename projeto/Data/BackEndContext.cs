using Microsoft.Extensions.Hosting;
using projeto.Data.Models;
using System.Data.Entity;

namespace projeto.Data
{
    public class BackEndContext : DbContext
    {
        public DbSet<Utilizadores> utilizadores {  get; set; }
        public DbSet<Tarefa> tarefas { get; set; }

        public DbSet<Servico> servicos { get; set; }

        public DbSet<RelatorioDesempenho> relatorios { get; set; }
        public DbSet<AlertaManutencao> alertas { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            //modelBuilder.Entity<Utilizadores>().HasMany(e => e.AlertaManutencao).WithMany(e => e.Utilizadores); 
            base.OnModelCreating(modelBuilder);

        }

    }
}
