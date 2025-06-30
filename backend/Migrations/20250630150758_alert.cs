using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class alert : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "alerts",
                columns: table => new
                {
                    ID = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    description = table.Column<string>(type: "text", nullable: false),
                    timestamp = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    adminID = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_alerts", x => x.ID);
                    table.ForeignKey(
                        name: "FK_alerts_users_adminID",
                        column: x => x.adminID,
                        principalTable: "users",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "AlertUser",
                columns: table => new
                {
                    targetedAlertsID = table.Column<int>(type: "integer", nullable: false),
                    targetsID = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_AlertUser", x => new { x.targetedAlertsID, x.targetsID });
                    table.ForeignKey(
                        name: "FK_AlertUser_alerts_targetedAlertsID",
                        column: x => x.targetedAlertsID,
                        principalTable: "alerts",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_AlertUser_users_targetsID",
                        column: x => x.targetsID,
                        principalTable: "users",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_alerts_adminID",
                table: "alerts",
                column: "adminID");

            migrationBuilder.CreateIndex(
                name: "IX_AlertUser_targetsID",
                table: "AlertUser",
                column: "targetsID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "AlertUser");

            migrationBuilder.DropTable(
                name: "alerts");
        }
    }
}
