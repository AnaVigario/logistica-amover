namespace projeto.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class creation : DbMigration
    {
        public override void Up()
        {
            CreateTable(
                "dbo.Alerts",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        description = c.String(nullable: false),
                        timestamp = c.DateTime(nullable: false),
                        User_ID = c.Int(),
                        linked_admin_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Users", t => t.User_ID)
                .ForeignKey("dbo.Users", t => t.linked_admin_ID)
                .Index(t => t.User_ID)
                .Index(t => t.linked_admin_ID);
            
            CreateTable(
                "dbo.Users",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        name = c.String(nullable: false),
                        email = c.String(nullable: false),
                        password = c.String(nullable: false),
                        type = c.String(nullable: false),
                        Alert_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Alerts", t => t.Alert_ID)
                .Index(t => t.Alert_ID);
            
            CreateTable(
                "dbo.Services",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        type = c.String(nullable: false),
                        date = c.DateTime(nullable: false),
                        description = c.String(nullable: false),
                        status = c.String(nullable: false),
                        user_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Users", t => t.user_ID)
                .Index(t => t.user_ID);
            
            CreateTable(
                "dbo.Tasks",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        type = c.String(nullable: false),
                        creation_date = c.DateTime(nullable: false),
                        deadline = c.DateTime(nullable: false),
                        description = c.String(nullable: false),
                        status = c.String(nullable: false),
                        service_ID = c.Int(),
                        Task_ID = c.Int(),
                        users_ID = c.Int(),
                    })
                .PrimaryKey(t => t.ID)
                .ForeignKey("dbo.Services", t => t.service_ID)
                .ForeignKey("dbo.Tasks", t => t.Task_ID)
                .ForeignKey("dbo.Users", t => t.users_ID)
                .Index(t => t.service_ID)
                .Index(t => t.Task_ID)
                .Index(t => t.users_ID);
            
            CreateTable(
                "dbo.PerformanceReports",
                c => new
                    {
                        ID = c.Int(nullable: false, identity: true),
                        description = c.String(nullable: false),
                        timestamp = c.DateTime(nullable: false),
                    })
                .PrimaryKey(t => t.ID);
            
        }
        
        public override void Down()
        {
            DropForeignKey("dbo.Users", "Alert_ID", "dbo.Alerts");
            DropForeignKey("dbo.Alerts", "linked_admin_ID", "dbo.Users");
            DropForeignKey("dbo.Services", "user_ID", "dbo.Users");
            DropForeignKey("dbo.Tasks", "users_ID", "dbo.Users");
            DropForeignKey("dbo.Tasks", "Task_ID", "dbo.Tasks");
            DropForeignKey("dbo.Tasks", "service_ID", "dbo.Services");
            DropForeignKey("dbo.Alerts", "User_ID", "dbo.Users");
            DropIndex("dbo.Tasks", new[] { "users_ID" });
            DropIndex("dbo.Tasks", new[] { "Task_ID" });
            DropIndex("dbo.Tasks", new[] { "service_ID" });
            DropIndex("dbo.Services", new[] { "user_ID" });
            DropIndex("dbo.Users", new[] { "Alert_ID" });
            DropIndex("dbo.Alerts", new[] { "linked_admin_ID" });
            DropIndex("dbo.Alerts", new[] { "User_ID" });
            DropTable("dbo.PerformanceReports");
            DropTable("dbo.Tasks");
            DropTable("dbo.Services");
            DropTable("dbo.Users");
            DropTable("dbo.Alerts");
        }
    }
}
