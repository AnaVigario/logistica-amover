using Microsoft.EntityFrameworkCore;
using projeto.Data.Models;

namespace projeto.Data
{
    public class AMoverContext : DbContext
    {
        public AMoverContext(DbContextOptions<AMoverContext> options)
        : base(options)
        {
        }

        public DbSet<User> users { get; set; }
        public DbSet<Vehicle> vehicles { get; set; }
        //public DbSet<Models.Task> tasks { get; set; }
        //public DbSet<Service> services { get; set; }
        //public DbSet<PerformanceReport> reports { get; set; }
        public DbSet<Alert> alerts { get; set; }
        public DbSet<Models.Route> routes { get; set; }
        public DbSet<LocationNode> locationNodes { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>()
                .HasMany(u => u.vehicles)
                .WithOne(v => v.owner)
                .HasForeignKey(v => v.ownerID)
                .OnDelete(DeleteBehavior.SetNull);
            modelBuilder.Entity<User>()
                .HasMany(u => u.targetedAlerts)
                .WithMany(a => a.targets);
            modelBuilder.Entity<User>()
                .HasMany(u => u.managedAlerts)
                .WithOne(a => a.admin)
                .HasForeignKey(a => a.adminID)
                .OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Data.Models.Task>()
                .HasMany(t => t.subTasks)
                .WithOne(t => t.parentTask)
                .HasForeignKey(t => t.parentTaskID);
            modelBuilder.Entity<Data.Models.Task>()
                .HasOne(t => t.service)
                .WithMany(s => s.tasks)
                .HasForeignKey(t => t.serviceID)
                .OnDelete(DeleteBehavior.Cascade);
            modelBuilder.Entity<Models.Route>()
                .HasMany(r => r.nodes)
                .WithMany(n => n.routes);
            base.OnModelCreating(modelBuilder);
        }
    }
}
