using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class taskNodes : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "LocationNodeTask",
                columns: table => new
                {
                    NodesID = table.Column<int>(type: "integer", nullable: false),
                    tasksID = table.Column<int>(type: "integer", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_LocationNodeTask", x => new { x.NodesID, x.tasksID });
                    table.ForeignKey(
                        name: "FK_LocationNodeTask_LocationNode_NodesID",
                        column: x => x.NodesID,
                        principalTable: "LocationNode",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_LocationNodeTask_tasks_tasksID",
                        column: x => x.tasksID,
                        principalTable: "tasks",
                        principalColumn: "ID",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_LocationNodeTask_tasksID",
                table: "LocationNodeTask",
                column: "tasksID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "LocationNodeTask");
        }
    }
}
