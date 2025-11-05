using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class ClientNN : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Task_Task_parentTaskID",
                table: "Task");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_users_userID",
                table: "Task");

            migrationBuilder.AlterColumn<int>(
                name: "userID",
                table: "Task",
                type: "integer",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "integer");

            migrationBuilder.AlterColumn<int>(
                name: "parentTaskID",
                table: "Task",
                type: "integer",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "integer");

            migrationBuilder.AddForeignKey(
                name: "FK_Task_Task_parentTaskID",
                table: "Task",
                column: "parentTaskID",
                principalTable: "Task",
                principalColumn: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_Task_users_userID",
                table: "Task",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Task_Task_parentTaskID",
                table: "Task");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_users_userID",
                table: "Task");

            migrationBuilder.AlterColumn<int>(
                name: "userID",
                table: "Task",
                type: "integer",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "integer",
                oldNullable: true);

            migrationBuilder.AlterColumn<int>(
                name: "parentTaskID",
                table: "Task",
                type: "integer",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "integer",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_Task_Task_parentTaskID",
                table: "Task",
                column: "parentTaskID",
                principalTable: "Task",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Task_users_userID",
                table: "Task",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
