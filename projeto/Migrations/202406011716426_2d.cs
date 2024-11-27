namespace projeto.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _2d : DbMigration
    {
        public override void Up()
        {
            AddColumn("dbo.Tarefas", "DeadLine", c => c.DateTime(nullable: false));
        }
        
        public override void Down()
        {
            DropColumn("dbo.Tarefas", "DeadLine");
        }
    }
}
