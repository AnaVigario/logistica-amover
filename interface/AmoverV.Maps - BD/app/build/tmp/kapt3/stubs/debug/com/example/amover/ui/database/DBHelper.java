package com.example.amover.ui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.amover.model.TaskModel;
import com.example.amover.model.UserModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010J\u0016\u0010\u0012\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\u0013j\b\u0012\u0004\u0012\u00020\u000e`\u0014J\u000e\u0010\u0015\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0010J\u0016\u0010\u0016\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\u0013j\b\u0012\u0004\u0012\u00020\u000e`\u0014J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0007J\u0016\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0007J\u0012\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0016J\"\u0010!\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 2\u0006\u0010\"\u001a\u00020\u00102\u0006\u0010#\u001a\u00020\u0010H\u0016J\u0016\u0010$\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\r\u001a\u00020\u000eJ\u001e\u0010%\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0007R\u0019\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\t\u00a8\u0006&"}, d2 = {"Lcom/example/amover/ui/database/DBHelper;", "Landroid/database/sqlite/SQLiteOpenHelper;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "sql", "", "", "getSql", "()[Ljava/lang/String;", "[Ljava/lang/String;", "addTask", "", "taskModel", "Lcom/example/amover/model/TaskModel;", "deleteTask", "", "id", "getCheckTasks", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "getTask", "getTasks", "getUser", "Lcom/example/amover/model/UserModel;", "username", "password", "login", "", "onCreate", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "onUpgrade", "oldVersion", "newVersion", "updateTask", "updateUser", "app_debug"})
public final class DBHelper extends android.database.sqlite.SQLiteOpenHelper {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String[] sql = {"CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)", "INSERT INTO users (username, password) VALUES( \'user\', \'pass\')", "CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, address TEXT, name TEXT, status TEXT, timewindow TEXT, timerTask TEXT, dateTask TEXT, note TEXT, image TEXT, latitude TEXT, longitude TEXT)", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Recolha\',\'ECT - Polo 1\',\'UTAD\',\'Andamento\',\'No specific time windows\',\'09:00:00\',\'2025-01-02\',\'teste\', NULL,\'41.28863566712775\',\'-7.739067907230414\')", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Recolha\',\'R. D. Ant\u00f3nio Valente da Fonseca\',\'Neutr\u00e3o - Centro Radio-diagn\u00f3stico\',\'Conclu\u00eddo\',\'No specific time windows\',\'09:15:00\',\'2025-03-13\',\'Envelopes exames\', NULL,\'41.299445642422675\',\'-7.749621546632743\')", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Entrega\',\'Largo Visconde Almeid Ganat\',\'Farm\u00e1cia Barreira\',\'Por iniciar\',\'No specific time windows\',\'09:30:00\',\'2025-03-12\',\'Caixa 16Kg\', NULL,\'41.29858613918125\',\'-7.742791387321814\')", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Recolha\',\'Casa de Mateus, 5000-291\',\'Funda\u00e7\u00e3o da Casa de Mateus\',\'Por iniciar\',\'10:00:00-10:30:00\',\'10:00:00\',\'2025-03-11\',\'Envelope\', NULL,\'41.29691548193833\',\'-7.711944067790135\')", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Recolha\',\'Rua dos Tr\u00eas Lagares, n.\u00ba 15, 5000-577\',\'Unidade de Cuidados de Sa\u00fade Personalizados (UCSP) de Mateus\',\'Por iniciar\',\'No specific time windows\',\'10:30:00\',\'2025-03-09\',\'Caixa 3Kg\', NULL,\'41.30063963258702\',\'-7.721785733216758\')", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Entrega\',\'Av. Carvalho Ara\u00fajo, n.\u00ba 4, 5000-657\',\'C\u00e2mara Municipal de Vila Real\',\'Por iniciar\',\'No specific time windows\',\'11:00:00\',\'2025-03-10\',\'Envelope\', NULL,\'41.294394797512076\',\'-7.7460288247800175\')", "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES (\'Recolha\',\'ECT - Polo 1\',\'UTAD\',\'Andamento\',\'No specific time windows\',\'09:00:00\',\'2025-02-12\',\'teste\', NULL,\'41.28863566712775\',\'-7.739067907230414\')"};
    
    public DBHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null, null, null, 0);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String[] getSql() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.database.sqlite.SQLiteDatabase db) {
    }
    
    @java.lang.Override()
    public void onUpgrade(@org.jetbrains.annotations.Nullable()
    android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    
    public final long updateUser(int id, @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.amover.model.UserModel getUser(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return null;
    }
    
    public final boolean login(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    java.lang.String password) {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.example.amover.model.TaskModel> getTasks() {
        return null;
    }
    
    public final long addTask(@org.jetbrains.annotations.NotNull()
    com.example.amover.model.TaskModel taskModel) {
        return 0L;
    }
    
    public final int deleteTask(int id) {
        return 0;
    }
    
    public final int updateTask(int id, @org.jetbrains.annotations.NotNull()
    com.example.amover.model.TaskModel taskModel) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.example.amover.model.TaskModel getTask(int id) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.ArrayList<com.example.amover.model.TaskModel> getCheckTasks() {
        return null;
    }
}