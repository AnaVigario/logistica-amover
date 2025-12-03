using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class deRoute : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tasks_routes_routeID",
                table: "tasks");

            migrationBuilder.DropTable(
                name: "LocationNodeRoute");

            migrationBuilder.DropTable(
                name: "routes");

            migrationBuilder.DropIndex(
                name: "IX_tasks_routeID",
                table: "tasks");

            migrationBuilder.DropPrimaryKey(
                name: "PK_locationNodes",
                table: "locationNodes");

            migrationBuilder.DropColumn(
                name: "routeID",
                table: "tasks");

            migrationBuilder.RenameTable(
                name: "locationNodes",
                newName: "LocationNode");

            migrationBuilder.AddPrimaryKey(
                name: "PK_LocationNode",
                table: "LocationNode",
                column: "ID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropPrimaryKey(
                name: "PK_LocationNode",
                table: "LocationNode");

            migrationBuilder.RenameTable(
                name: "LocationNode",
                newName: "locationNodes");

            migrationBuilder.AddColumn<int>(
                name: "routeID",
                table: "tasks",
                type: "integer",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_locationNodes",
                table: "locationNodes",
                column: "ID");

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
                name: "LocationNodeRoute",
                columns: table => new
                {
                    nodesID = table.Column<int>(type: "integer", nullable: false),
                    routesID = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_LocationNodeRoute", x => new { x.nodesID, x.routesID });
                    table.ForeignKey(
                        name: "FK_LocationNodeRoute_locationNodes_nodesID",
                        column: x => x.nodesID,
                        principalTable: "locationNodes",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_LocationNodeRoute_routes_routesID",
                        column: x => x.routesID,
                        principalTable: "routes",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_tasks_routeID",
                table: "tasks",
                column: "routeID");

            migrationBuilder.CreateIndex(
                name: "IX_LocationNodeRoute_routesID",
                table: "LocationNodeRoute",
                column: "routesID");

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_routes_routeID",
                table: "tasks",
                column: "routeID",
                principalTable: "routes",
                principalColumn: "ID");
        }
    }
}
