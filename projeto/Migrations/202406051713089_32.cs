namespace projeto.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _32 : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Tarefas", "Tarefa_id_tarefa", c => c.Int());
            CreateIndex("dbo.Tarefas", "Tarefa_id_tarefa");
            AddForeignKey("dbo.Tarefas", "Tarefa_id_tarefa", "dbo.Tarefas", "id_tarefa");
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Tarefas", "Tarefa_id_tarefa", "dbo.Tarefas");
            DropIndex("dbo.Tarefas", new[] { "Tarefa_id_tarefa" });
            DropColumn("dbo.Tarefas", "Tarefa_id_tarefa");
        }
    }
}
