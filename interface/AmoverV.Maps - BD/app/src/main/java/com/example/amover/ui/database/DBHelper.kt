package com.example.amover.ui.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.amover.model.TaskModel
import com.example.amover.model.UserModel

class DBHelper(context: Context) : SQLiteOpenHelper(context, "database.db", null, 2) {

    val sql = arrayOf(
        "CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)",
        "INSERT INTO users (username, password) VALUES( 'user', 'pass')",
        "CREATE TABLE tasks(id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, address TEXT, name TEXT, status TEXT, timewindow TEXT, timerTask TEXT, dateTask TEXT, note TEXT, image TEXT, latitude TEXT, longitude TEXT)",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Recolha','ECT - Polo 1','UTAD','Andamento','No specific time windows','09:00:00','2025-01-02','teste', NULL,'41.28863566712775','-7.739067907230414')",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Recolha','R. D. António Valente da Fonseca','Neutrão - Centro Radio-diagnóstico','Concluído','No specific time windows','09:15:00','2025-03-13','Envelopes exames', NULL,'41.299445642422675','-7.749621546632743')",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Entrega','Largo Visconde Almeid Ganat','Farmácia Barreira','Por iniciar','No specific time windows','09:30:00','2025-03-12','Caixa 16Kg', NULL,'41.29858613918125','-7.742791387321814')",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Recolha','Casa de Mateus, 5000-291','Fundação da Casa de Mateus','Por iniciar','10:00:00-10:30:00','10:00:00','2025-03-11','Envelope', NULL,'41.29691548193833','-7.711944067790135')",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Recolha','Rua dos Três Lagares, n.º 15, 5000-577','Unidade de Cuidados de Saúde Personalizados (UCSP) de Mateus','Por iniciar','No specific time windows','10:30:00','2025-03-09','Caixa 3Kg', NULL,'41.30063963258702','-7.721785733216758')",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Entrega','Av. Carvalho Araújo, n.º 4, 5000-657','Câmara Municipal de Vila Real','Por iniciar','No specific time windows','11:00:00','2025-03-10','Envelope', NULL,'41.294394797512076','-7.7460288247800175')",
        "INSERT INTO tasks (type, address, name, status, timewindow, timerTask, dateTask, note, image, latitude, longitude) VALUES ('Recolha','ECT - Polo 1','UTAD','Andamento','No specific time windows','09:00:00','2025-02-12','teste', NULL,'41.28863566712775','-7.739067907230414')"

    )

    override fun onCreate(db: SQLiteDatabase?) {
        sql.forEach {
            if (db != null) {
                db.execSQL(it)
            }
            Log.d("DBHelper", "Tabela 'tasks' criada com sucesso.")

        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS users")
        db?.execSQL("DROP TABLE IF EXISTS tasks")
        onCreate(db)
    }


    //-------------------------- USER -----------------------------------

    fun updateUser(id: Int, username: String, password: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("username", username)
        contentValues.put("password", password)

        val res = db.update("users", contentValues, "id=?", arrayOf(id.toString()))
        db.close()

        return res.toLong()
    }

    fun getUser(username: String, password: String): UserModel {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        var userModel = UserModel()

        if (c.count == 1) {
            c.moveToFirst()
            val idIndex = c.getColumnIndex("id")
            val usernameIndex = c.getColumnIndex("username")
            val passwordIndex = c.getColumnIndex("password")

            userModel = UserModel(
                id = c.getInt(idIndex),
                username = c.getString(usernameIndex),
                password = c.getString(passwordIndex)
            )
        }
        db.close()
        return userModel
    }


    fun login(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val c = db.rawQuery(
            "SELECT * FROM users WHERE username=? AND password=?",
            arrayOf(username, password)
        )

        var userModel = UserModel()

        if (c.count == 1) {
            db.close()
            return true
        } else {
            db.close()
            return false
        }
    }

    ///-------------------------- TASKS -----------------------------------
    fun getTasks(): ArrayList<TaskModel> {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT * FROM tasks", null)
        val taskList = ArrayList<TaskModel>()
        if (c.moveToFirst()) {
            do {
                val idIndex = c.getColumnIndex("id")
                val typeIndex = c.getColumnIndex("type")
                val addressIndex = c.getColumnIndex("address")
                val nameIndex = c.getColumnIndex("name")
                val statusIndex = c.getColumnIndex("status")
                val timewindowIndex = c.getColumnIndex("timewindow")
                val timerTaskIndex = c.getColumnIndex("timerTask")
                val dateTaskIndex = c.getColumnIndex("dateTask")
                val noteIndex = c.getColumnIndex("note")
                val imageIndex = c.getColumnIndex("image")
                val latitudeIndex = c.getColumnIndex("latitude")
                val longitudeIndex = c.getColumnIndex("longitude")
                taskList.add(
                    TaskModel(
                        id = c.getInt(idIndex),
                        type = c.getString(typeIndex),
                        address = c.getString(addressIndex),
                        name = c.getString(nameIndex),
                        status = c.getString(statusIndex),
                        timewindow = c.getString(timewindowIndex),
                        timerTask = c.getString(timerTaskIndex),
                        dateTask = c.getString(dateTaskIndex),
                        note = c.getString(noteIndex),
                        image = c.getString(imageIndex),
                        latitude = c.getDouble(latitudeIndex),
                        longitude = c.getDouble(longitudeIndex)
                    )
                )
            } while (c.moveToNext())
        }
        db.close()
        return taskList
    }

    fun addTask(taskModel: TaskModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("id", taskModel.id)
            put("type", taskModel.type)
            put("address", taskModel.address)
            put("name", taskModel.name)
            put("status", taskModel.status)
            put("timewindow", taskModel.timewindow)
            put("timerTask", taskModel.timerTask)
            put("dateTask", taskModel.dateTask)
            put("note", taskModel.note)
            put("image", taskModel.image)
            put("latitude", taskModel.latitude)
            put("longitude", taskModel.longitude)
        }
        val res = db.insert("tasks", null, contentValues)
        db.close()
        return res
    }

    fun deleteTask(id: Int): Int {
        val db = this.writableDatabase
        val res = db.delete("tasks", "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun updateTask(id: Int, taskModel: TaskModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("type", taskModel.type)
        contentValues.put("address", taskModel.address)
        contentValues.put("name", taskModel.name)
        contentValues.put("status", taskModel.status)
        contentValues.put("timewindow", taskModel.timewindow)
        contentValues.put("timerTask", taskModel.timerTask)
        contentValues.put("dateTask", taskModel.dateTask)
        contentValues.put("note", taskModel.note)
        contentValues.put("image", taskModel.image)
        contentValues.put("latitude", taskModel.latitude)
        contentValues.put("longitude", taskModel.longitude)
        val res = db.update("tasks", contentValues, "id=?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun getTask(id: Int): TaskModel {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT * FROM tasks WHERE id=?", arrayOf(id.toString()))
        val taskModel = TaskModel()
        if (c.moveToFirst()) {
            val idIndex = c.getColumnIndexOrThrow("id")
            val typeIndex = c.getColumnIndexOrThrow("type")
            val addressIndex = c.getColumnIndexOrThrow("address")
            val nameIndex = c.getColumnIndexOrThrow("name")
            val statusIndex = c.getColumnIndexOrThrow("status")
            val timewindowIndex = c.getColumnIndexOrThrow("timewindow")
            val timerTaskIndex = c.getColumnIndexOrThrow("timerTask")
            val dateTaskIndex = c.getColumnIndexOrThrow("dateTask")
            val noteIndex = c.getColumnIndexOrThrow("note")
            val imageIndex = c.getColumnIndexOrThrow("image")
            val latitudeIndex = c.getColumnIndexOrThrow("latitude")
            val longitudeIndex = c.getColumnIndexOrThrow("longitude")
            taskModel
            return TaskModel(
                id = c.getInt(idIndex),
                type = c.getString(typeIndex),
                address = c.getString(addressIndex),
                name = c.getString(nameIndex),
                status = c.getString(statusIndex),
                timewindow = c.getString(timewindowIndex),
                timerTask = c.getString(timerTaskIndex),
                dateTask = c.getString(dateTaskIndex),
                note = c.getString(noteIndex),
                image = c.getString(imageIndex),
                latitude = c.getDouble(latitudeIndex),
                longitude = c.getDouble(longitudeIndex)
            )
        }
        c.close()
        db.close()
        return taskModel
    }

    fun getCheckTasks(): ArrayList<TaskModel> {
        val db = this.readableDatabase
        val c = db.rawQuery("SELECT * FROM tasks ORDER BY dateTask ASC", null)
        val taskcheckList = ArrayList<TaskModel>()

        if (c.moveToFirst()) {
            do {
                val taskcheck = TaskModel(
                    id = c.getInt(c.getColumnIndexOrThrow("id")),
                    type = c.getString(c.getColumnIndexOrThrow("type")),
                    address = c.getString(c.getColumnIndexOrThrow("address")),
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    status = c.getString(c.getColumnIndexOrThrow("status")),
                    timewindow = c.getString(c.getColumnIndexOrThrow("timewindow")),
                    timerTask = c.getString(c.getColumnIndexOrThrow("timerTask")),
                    dateTask = c.getString(c.getColumnIndexOrThrow("dateTask")),
                    note = c.getString(c.getColumnIndexOrThrow("note")),
                    image = c.getString(c.getColumnIndexOrThrow("image")),
                    latitude = c.getDouble(c.getColumnIndexOrThrow("latitude")),
                    longitude = c.getDouble(c.getColumnIndexOrThrow("longitude"))
                )
                taskcheckList.add(taskcheck)
            } while (c.moveToNext())
        }

        c.close()
        db.close()
        return taskcheckList
    }
}



 