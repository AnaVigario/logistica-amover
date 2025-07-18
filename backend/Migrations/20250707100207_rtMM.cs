using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class rtMM : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_locationNodes_routes_routeID",
                table: "locationNodes");

            migrationBuilder.DropIndex(
                name: "IX_locationNodes_routeID",
                table: "locationNodes");

            migrationBuilder.DropColumn(
                name: "routeID",
                table: "locationNodes");

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
                name: "IX_LocationNodeRoute_routesID",
                table: "LocationNodeRoute",
                column: "routesID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "LocationNodeRoute");

            migrationBuilder.AddColumn<int>(
                name: "routeID",
                table: "locationNodes",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_locationNodes_routeID",
                table: "locationNodes",
                column: "routeID");

            migrationBuilder.AddForeignKey(
                name: "FK_locationNodes_routes_routeID",
                table: "locationNodes",
                column: "routeID",
                principalTable: "routes",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
