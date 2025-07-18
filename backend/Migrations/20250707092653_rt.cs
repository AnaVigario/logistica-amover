using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class rt : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "serviceID",
                table: "Task",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.CreateTable(
                name: "routes",
                columns: table => new
                {
                    ID = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    description = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_routes", x => x.ID);
                });

            migrationBuilder.CreateTable(
                name: "Service",
                columns: table => new
                {
                    ID = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    type = table.Column<string>(type: "text", nullable: false),
                    description = table.Column<string>(type: "text", nullable: false),
                    date = table.Column<DateTime>(type: "timestamp with time zone", nullable: false),
                    status = table.Column<string>(type: "text", nullable: false),
                    userID = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Service", x => x.ID);
                    table.ForeignKey(
                        name: "FK_Service_users_userID",
                        column: x => x.userID,
                        principalTable: "users",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "locationNodes",
                columns: table => new
                {
                    ID = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    latitude = table.Column<float>(type: "real", nullable: false),
                    longintude = table.Column<float>(type: "real", nullable: false),
                    routeID = table.Column<int>(type: "integer", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_locationNodes", x => x.ID);
                    table.ForeignKey(
                        name: "FK_locationNodes_routes_routeID",
                        column: x => x.routeID,
                        principalTable: "routes",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_Task_serviceID",
                table: "Task",
                column: "serviceID");

            migrationBuilder.CreateIndex(
                name: "IX_locationNodes_routeID",
                table: "locationNodes",
                column: "routeID");

            migrationBuilder.CreateIndex(
                name: "IX_Service_userID",
                table: "Service",
                column: "userID");

            migrationBuilder.AddForeignKey(
                name: "FK_Task_Service_serviceID",
                table: "Task",
                column: "serviceID",
                principalTable: "Service",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Task_Service_serviceID",
                table: "Task");

            migrationBuilder.DropTable(
                name: "locationNodes");

            migrationBuilder.DropTable(
                name: "Service");

            migrationBuilder.DropTable(
                name: "routes");

            migrationBuilder.DropIndex(
                name: "IX_Task_serviceID",
                table: "Task");

            migrationBuilder.DropColumn(
                name: "serviceID",
                table: "Task");
        }
    }
}
