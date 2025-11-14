using System;
using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class Company1 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Service_users_userID",
                table: "Service");

            migrationBuilder.DropColumn(
                name: "date",
                table: "Service");

            migrationBuilder.DropColumn(
                name: "status",
                table: "Service");

            migrationBuilder.RenameColumn(
                name: "userID",
                table: "Service",
                newName: "companyID");

            migrationBuilder.RenameColumn(
                name: "type",
                table: "Service",
                newName: "category");

            migrationBuilder.RenameIndex(
                name: "IX_Service_userID",
                table: "Service",
                newName: "IX_Service_companyID");

            migrationBuilder.AddColumn<int>(
                name: "companyID",
                table: "users",
                type: "integer",
                nullable: true);

            migrationBuilder.CreateTable(
                name: "companies",
                columns: table => new
                {
                    ID = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    name = table.Column<string>(type: "text", nullable: false),
                    description = table.Column<string>(type: "text", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_companies", x => x.ID);
                });

            migrationBuilder.CreateIndex(
                name: "IX_users_companyID",
                table: "users",
                column: "companyID");

            migrationBuilder.AddForeignKey(
                name: "FK_Service_companies_companyID",
                table: "Service",
                column: "companyID",
                principalTable: "companies",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_users_companies_companyID",
                table: "users",
                column: "companyID",
                principalTable: "companies",
                principalColumn: "ID",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Service_companies_companyID",
                table: "Service");

            migrationBuilder.DropForeignKey(
                name: "FK_users_companies_companyID",
                table: "users");

            migrationBuilder.DropTable(
                name: "companies");

            migrationBuilder.DropIndex(
                name: "IX_users_companyID",
                table: "users");

            migrationBuilder.DropColumn(
                name: "companyID",
                table: "users");

            migrationBuilder.RenameColumn(
                name: "companyID",
                table: "Service",
                newName: "userID");

            migrationBuilder.RenameColumn(
                name: "category",
                table: "Service",
                newName: "type");

            migrationBuilder.RenameIndex(
                name: "IX_Service_companyID",
                table: "Service",
                newName: "IX_Service_userID");

            migrationBuilder.AddColumn<DateTime>(
                name: "date",
                table: "Service",
                type: "timestamp with time zone",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));

            migrationBuilder.AddColumn<string>(
                name: "status",
                table: "Service",
                type: "text",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddForeignKey(
                name: "FK_Service_users_userID",
                table: "Service",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
