using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    /// <inheritdoc />
    public partial class test1 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Service_companies_companyID",
                table: "Service");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_Service_serviceID",
                table: "Task");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_Task_parentTaskID",
                table: "Task");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_clients_clientID",
                table: "Task");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_routes_routeID",
                table: "Task");

            migrationBuilder.DropForeignKey(
                name: "FK_Task_users_userID",
                table: "Task");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Task",
                table: "Task");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Service",
                table: "Service");

            migrationBuilder.RenameTable(
                name: "Task",
                newName: "tasks");

            migrationBuilder.RenameTable(
                name: "Service",
                newName: "services");

            migrationBuilder.RenameIndex(
                name: "IX_Task_userID",
                table: "tasks",
                newName: "IX_tasks_userID");

            migrationBuilder.RenameIndex(
                name: "IX_Task_serviceID",
                table: "tasks",
                newName: "IX_tasks_serviceID");

            migrationBuilder.RenameIndex(
                name: "IX_Task_routeID",
                table: "tasks",
                newName: "IX_tasks_routeID");

            migrationBuilder.RenameIndex(
                name: "IX_Task_parentTaskID",
                table: "tasks",
                newName: "IX_tasks_parentTaskID");

            migrationBuilder.RenameIndex(
                name: "IX_Task_clientID",
                table: "tasks",
                newName: "IX_tasks_clientID");

            migrationBuilder.RenameIndex(
                name: "IX_Service_companyID",
                table: "services",
                newName: "IX_services_companyID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_tasks",
                table: "tasks",
                column: "ID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_services",
                table: "services",
                column: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_services_companies_companyID",
                table: "services",
                column: "companyID",
                principalTable: "companies",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_clients_clientID",
                table: "tasks",
                column: "clientID",
                principalTable: "clients",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_routes_routeID",
                table: "tasks",
                column: "routeID",
                principalTable: "routes",
                principalColumn: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_services_serviceID",
                table: "tasks",
                column: "serviceID",
                principalTable: "services",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_tasks_parentTaskID",
                table: "tasks",
                column: "parentTaskID",
                principalTable: "tasks",
                principalColumn: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_tasks_users_userID",
                table: "tasks",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_services_companies_companyID",
                table: "services");

            migrationBuilder.DropForeignKey(
                name: "FK_tasks_clients_clientID",
                table: "tasks");

            migrationBuilder.DropForeignKey(
                name: "FK_tasks_routes_routeID",
                table: "tasks");

            migrationBuilder.DropForeignKey(
                name: "FK_tasks_services_serviceID",
                table: "tasks");

            migrationBuilder.DropForeignKey(
                name: "FK_tasks_tasks_parentTaskID",
                table: "tasks");

            migrationBuilder.DropForeignKey(
                name: "FK_tasks_users_userID",
                table: "tasks");

            migrationBuilder.DropPrimaryKey(
                name: "PK_tasks",
                table: "tasks");

            migrationBuilder.DropPrimaryKey(
                name: "PK_services",
                table: "services");

            migrationBuilder.RenameTable(
                name: "tasks",
                newName: "Task");

            migrationBuilder.RenameTable(
                name: "services",
                newName: "Service");

            migrationBuilder.RenameIndex(
                name: "IX_tasks_userID",
                table: "Task",
                newName: "IX_Task_userID");

            migrationBuilder.RenameIndex(
                name: "IX_tasks_serviceID",
                table: "Task",
                newName: "IX_Task_serviceID");

            migrationBuilder.RenameIndex(
                name: "IX_tasks_routeID",
                table: "Task",
                newName: "IX_Task_routeID");

            migrationBuilder.RenameIndex(
                name: "IX_tasks_parentTaskID",
                table: "Task",
                newName: "IX_Task_parentTaskID");

            migrationBuilder.RenameIndex(
                name: "IX_tasks_clientID",
                table: "Task",
                newName: "IX_Task_clientID");

            migrationBuilder.RenameIndex(
                name: "IX_services_companyID",
                table: "Service",
                newName: "IX_Service_companyID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Task",
                table: "Task",
                column: "ID");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Service",
                table: "Service",
                column: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_Service_companies_companyID",
                table: "Service",
                column: "companyID",
                principalTable: "companies",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Task_Service_serviceID",
                table: "Task",
                column: "serviceID",
                principalTable: "Service",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Task_Task_parentTaskID",
                table: "Task",
                column: "parentTaskID",
                principalTable: "Task",
                principalColumn: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_Task_clients_clientID",
                table: "Task",
                column: "clientID",
                principalTable: "clients",
                principalColumn: "ID",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Task_routes_routeID",
                table: "Task",
                column: "routeID",
                principalTable: "routes",
                principalColumn: "ID");

            migrationBuilder.AddForeignKey(
                name: "FK_Task_users_userID",
                table: "Task",
                column: "userID",
                principalTable: "users",
                principalColumn: "ID");
        }
    }
}
