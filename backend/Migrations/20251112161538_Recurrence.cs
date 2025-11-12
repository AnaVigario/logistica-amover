using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class Recurrence : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "recurrence",
                table: "Task",
                type: "integer",
                nullable: false,
                defaultValue: 0);
            migrationBuilder.AddCheckConstraint(
                name: "CK_Task_Recurrence_Enum",
                table: "Task",
                sql: "recurrence IN ('None', 'Daily', 'Weekly')"
);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "recurrence",
                table: "Task");
        }
    }
}
