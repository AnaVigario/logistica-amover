using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class rt1 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "description",
                table: "locationNodes",
                type: "text",
                nullable: false,
                defaultValue: "");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "description",
                table: "locationNodes");
        }
    }
}
