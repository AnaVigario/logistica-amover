﻿// <auto-generated />
using System;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;
using projeto.Data;

#nullable disable

namespace AMoVeRLogistica.Migrations
{
    [DbContext(typeof(AMoverContext))]
    [Migration("20250630150758_alert")]
    partial class alert
    {
        /// <inheritdoc />
        protected override void BuildTargetModel(ModelBuilder modelBuilder)
        {
#pragma warning disable 612, 618
            modelBuilder
                .HasAnnotation("ProductVersion", "9.0.6")
                .HasAnnotation("Relational:MaxIdentifierLength", 63);

            NpgsqlModelBuilderExtensions.UseIdentityByDefaultColumns(modelBuilder);

            modelBuilder.Entity("AlertUser", b =>
                {
                    b.Property<int>("targetedAlertsID")
                        .HasColumnType("integer");

                    b.Property<int>("targetsID")
                        .HasColumnType("integer");

                    b.HasKey("targetedAlertsID", "targetsID");

                    b.HasIndex("targetsID");

                    b.ToTable("AlertUser");
                });

            modelBuilder.Entity("projeto.Data.Models.Alert", b =>
                {
                    b.Property<int>("ID")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("ID"));

                    b.Property<int>("adminID")
                        .HasColumnType("integer");

                    b.Property<string>("description")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<DateTime>("timestamp")
                        .HasColumnType("timestamp with time zone");

                    b.HasKey("ID");

                    b.HasIndex("adminID");

                    b.ToTable("alerts");
                });

            modelBuilder.Entity("projeto.Data.Models.User", b =>
                {
                    b.Property<int>("ID")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("ID"));

                    b.Property<string>("email")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("name")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("password")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<string>("type")
                        .IsRequired()
                        .HasColumnType("text");

                    b.HasKey("ID");

                    b.ToTable("users");
                });

            modelBuilder.Entity("projeto.Data.Models.Vehicle", b =>
                {
                    b.Property<int>("ID")
                        .ValueGeneratedOnAdd()
                        .HasColumnType("integer");

                    NpgsqlPropertyBuilderExtensions.UseIdentityByDefaultColumn(b.Property<int>("ID"));

                    b.Property<string>("VID")
                        .IsRequired()
                        .HasColumnType("text");

                    b.Property<int>("ownerID")
                        .HasColumnType("integer");

                    b.HasKey("ID");

                    b.HasIndex("ownerID");

                    b.ToTable("vehicles");
                });

            modelBuilder.Entity("AlertUser", b =>
                {
                    b.HasOne("projeto.Data.Models.Alert", null)
                        .WithMany()
                        .HasForeignKey("targetedAlertsID")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.HasOne("projeto.Data.Models.User", null)
                        .WithMany()
                        .HasForeignKey("targetsID")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();
                });

            modelBuilder.Entity("projeto.Data.Models.Alert", b =>
                {
                    b.HasOne("projeto.Data.Models.User", "admin")
                        .WithMany("managedAlerts")
                        .HasForeignKey("adminID")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("admin");
                });

            modelBuilder.Entity("projeto.Data.Models.Vehicle", b =>
                {
                    b.HasOne("projeto.Data.Models.User", "owner")
                        .WithMany("vehicles")
                        .HasForeignKey("ownerID")
                        .OnDelete(DeleteBehavior.Cascade)
                        .IsRequired();

                    b.Navigation("owner");
                });

            modelBuilder.Entity("projeto.Data.Models.User", b =>
                {
                    b.Navigation("managedAlerts");

                    b.Navigation("vehicles");
                });
#pragma warning restore 612, 618
        }
    }
}
