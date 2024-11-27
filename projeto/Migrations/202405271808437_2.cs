namespace projeto.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class _2 : DbMigration
    {
        public override void Up()
        {
            DropForeignKey("dbo.AlertaManutencaos", "sistema_id_sistema", "dbo.Sistemas");
            DropForeignKey("dbo.RelatorioDesempenhoes", "sistema_id_sistema", "dbo.Sistemas");
            DropIndex("dbo.AlertaManutencaos", new[] { "sistema_id_sistema" });
            DropIndex("dbo.RelatorioDesempenhoes", new[] { "sistema_id_sistema" });
            DropColumn("dbo.AlertaManutencaos", "sistema_id_sistema");
            DropColumn("dbo.RelatorioDesempenhoes", "sistema_id_sistema");
            DropTable("dbo.Sistemas");
        }
        
        public override void Down()
        {
            CreateTable(
                "dbo.Sistemas",
                c => new
                    {
                        id_sistema = c.Int(nullable: false, identity: true),
                    })
                .PrimaryKey(t => t.id_sistema);
            
            AddColumn("dbo.RelatorioDesempenhoes", "sistema_id_sistema", c => c.Int());
            AddColumn("dbo.AlertaManutencaos", "sistema_id_sistema", c => c.Int());
            CreateIndex("dbo.RelatorioDesempenhoes", "sistema_id_sistema");
            CreateIndex("dbo.AlertaManutencaos", "sistema_id_sistema");
            AddForeignKey("dbo.RelatorioDesempenhoes", "sistema_id_sistema", "dbo.Sistemas", "id_sistema");
            AddForeignKey("dbo.AlertaManutencaos", "sistema_id_sistema", "dbo.Sistemas", "id_sistema");
        }
    }
}
