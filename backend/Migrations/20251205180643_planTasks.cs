using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class planTasks : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tasks_users_userID",
                table: "tasks");

            migrationBuilder.AddColumn<int>(
                name: "planID",
                table: "tasks",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_tasks_planID",
                table: "tasks",
                column: "planID");

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_Plan_planID",
                table: "tasks",
                column: "planID",
                principalTable: "Plan",
                principalColumn: "ID",
                onDelete: ReferentialAction.SetNull);

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_users_userID",
                table: "tasks",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_tasks_Plan_planID",
                table: "tasks");

            migrationBuilder.DropForeignKey(
                name: "FK_tasks_users_userID",
                table: "tasks");

            migrationBuilder.DropIndex(
                name: "IX_tasks_planID",
                table: "tasks");

            migrationBuilder.DropColumn(
                name: "planID",
                table: "tasks");

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_users_userID",
                table: "tasks",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID");
        }
    }
}
