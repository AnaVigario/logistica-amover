using Microsoft.Extensions.Hosting;
using projeto.Data.Models;
using System.Data.Entity;

namespace projeto.Data
{
    public class BackEndContext : DbContext
    {
        public DbSet<User> users { get; set; }
        public DbSet<Models.Task> tasks { get; set; }
        public DbSet<Service> services { get; set; }
        public DbSet<PerformanceReport> reports { get; set; }
        public DbSet<Alert> alerts { get; set; }
        public DbSet<Vehicle> vehicles { get; set; }

        protected override void OnModelCreating(DbModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
        }
    }
}
