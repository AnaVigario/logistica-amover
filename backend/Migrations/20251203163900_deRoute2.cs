using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class deRoute2 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tasks_tasks_parentTaskID",
                table: "tasks");

            migrationBuilder.DropIndex(
                name: "IX_tasks_parentTaskID",
                table: "tasks");

            migrationBuilder.DropColumn(
                name: "parentTaskID",
                table: "tasks");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "parentTaskID",
                table: "tasks",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_tasks_parentTaskID",
                table: "tasks",
                column: "parentTaskID");

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_tasks_parentTaskID",
                table: "tasks",
                column: "parentTaskID",
                principalTable: "tasks",
                principalColumn: "ID");
        }
    }
}
