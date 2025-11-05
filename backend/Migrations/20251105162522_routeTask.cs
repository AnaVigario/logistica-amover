using System.Collections.Generic;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class routeTask : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "coordinates",
                table: "Task");

            migrationBuilder.RenameColumn(
                name: "description",
                table: "locationNodes",
                newName: "address");

            migrationBuilder.AddColumn<int>(
                name: "routeID",
                table: "Task",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Task_routeID",
                table: "Task",
                column: "routeID");

            migrationBuilder.AddForeignKey(
                name: "FK_Task_routes_routeID",
                table: "Task",
                column: "routeID",
                principalTable: "routes",
                principalColumn: "ID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Task_routes_routeID",
                table: "Task");

            migrationBuilder.DropIndex(
                name: "IX_Task_routeID",
                table: "Task");

            migrationBuilder.DropColumn(
                name: "routeID",
                table: "Task");

            migrationBuilder.RenameColumn(
                name: "address",
                table: "locationNodes",
                newName: "description");

            migrationBuilder.AddColumn<List<string>>(
                name: "coordinates",
                table: "Task",
                type: "text[]",
                nullable: false);
        }
    }
}
