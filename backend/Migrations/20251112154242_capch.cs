using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class capch : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<float>(
                name: "batteryCapacity",
                table: "vehicles",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.AddColumn<float>(
                name: "cargoCapacity",
                table: "vehicles",
                type: "real",
                nullable: false,
                defaultValue: 0f);

            migrationBuilder.DropColumn(
                name: "availableTimeStart",
                table: "Task");

            migrationBuilder.AddColumn<TimeSpan>(
                name: "availableTimeStart",
                table: "Task",
                type: "interval",
                nullable: true);

            migrationBuilder.DropColumn(
                name: "availableTimeEnds",
                table: "Task");

            migrationBuilder.AddColumn<TimeSpan>(
                name: "availableTimeEnds",
                table: "Task",
                type: "interval",
                nullable: true);

            migrationBuilder.AddColumn<TimeSpan>(
                name: "availableTimeEnds",
                table: "locationNodes",
                type: "interval",
                nullable: true);

            migrationBuilder.AddColumn<TimeSpan>(
                name: "availableTimeStart",
                table: "locationNodes",
                type: "interval",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "batteryCapacity",
                table: "vehicles");

            migrationBuilder.DropColumn(
                name: "cargoCapacity",
                table: "vehicles");

            migrationBuilder.DropColumn(
                name: "availableTimeEnds",
                table: "locationNodes");

            migrationBuilder.DropColumn(
                name: "availableTimeStart",
                table: "locationNodes");

            migrationBuilder.AlterColumn<DateTime>(
                name: "availableTimeStart",
                table: "Task",
                type: "timestamp with time zone",
                nullable: true,
                oldClrType: typeof(TimeSpan),
                oldType: "interval",
                oldNullable: true);

            migrationBuilder.AlterColumn<DateTime>(
                name: "availableTimeEnds",
                table: "Task",
                type: "timestamp with time zone",
                nullable: true,
                oldClrType: typeof(TimeSpan),
                oldType: "interval",
                oldNullable: true);
        }
    }
}
