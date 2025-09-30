package com.example.myamover.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myamover.data.dao.ClientDao
import com.example.myamover.data.dao.TaskDao
import com.example.myamover.data.dao.UserDao
import com.example.myamover.data.entities.ClientEntity
import com.example.myamover.data.entities.Priority
import com.example.myamover.data.entities.PriorityConverter
import com.example.myamover.data.entities.TaskEntity
import com.example.myamover.data.entities.UserEntity
import com.example.myamover.di.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserEntity::class, TaskEntity::class, ClientEntity::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2
        )
    ],

)

@TypeConverters(PriorityConverter::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun clientDao(): ClientDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    .addCallback(SeedCallback(context.applicationContext))
                    .build()
                    .also { INSTANCE = it }


            }
    }

    private class SeedCallback(private val context:  Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            CoroutineScope(Dispatchers.IO).launch {
                val instance = getInstance(context)
                val userDao = instance.userDao()
                val taskDao = instance.taskDao()
                val clientDao = instance.clientDao()

                // 1) Utilizador demo
                val saltB64 = PasswordHasher.newSaltBase64()
                val hashB64 = PasswordHasher.hashBase64("admin123", saltB64)
                try {
                    userDao.insert(
                        UserEntity(
                            email = "amover@amover.pt",
                            passwordHash = hashB64,
                            salt = saltB64,
                            name = "Amover"
                        )
                    )
                    val utadId = clientDao.insertClient(
                        ClientEntity(
                            name = "UTAD",
                            nif = "900000000",
                            address = "Quinta dos Prados, 5100-307, Vila Real",
                            phone = "259350000",
                            email = "servicos@utad.pt"
                        )
                    )
                    val neutraoId = clientDao.insertClient(
                        ClientEntity(
                            name = "Neutrão - Centro Radio-diagnóstico",
                            nif = "900000001",
                            address = "R. D. António Valente da Fonseca",
                            phone = "259322025",
                            email = "neutrao@beta.pt"
                        )
                    )
                    val barreiraId = clientDao.insertClient(
                        ClientEntity(
                            name = "Farmácia Barreira",
                            nif = "900000002",
                            address = "Largo Visconde Almeid Gant",
                            phone = "259322862",
                            email = "farmacia@gamma.pt"
                        )
                    )
                    taskDao.insert(
                        TaskEntity(
                            /*id = 0,*/
                            type = "Entrega",
                            creation_date= 1759233600000L, // 30-09-2025 12:00 UTC
                            deadline = 1759239000000L, // 30-09-2025 13:30 UTC
                            time_window = "No specific time windows",
                            description = "Exames",
                            /* done = false, */
                            status = "Em andamento",
                            pickup_location = "Quinta dos Prados, ECT, Polo I",
                            delivery_location = "Reitoria",
                            longitude = 41.2886,
                            latitude =-7.7391,
                            priority = Priority.HIGH,
                            client_id = 1)
                    )
                    taskDao.insert(
                        TaskEntity(
                            /*id = 1,*/
                            type = "Recolha",
                            creation_date= 1741597200000L, //10-03-2025 09:00
                            deadline = 1741599000000L, //10-03-2025 09:30
                            time_window = "No specific time windows",
                            description = "Envelopes",
                            /* done = false, */
                            status = "Concluído",
                            pickup_location = "R. D. António Valente da Fonseca",
                            delivery_location = "Largo Visconde Almeid Ganat",
                            longitude = 41.2994,
                            latitude =-7.7496,
                            priority = Priority.LOW,
                            client_id = 1)
                    )
                    taskDao.insert(
                        TaskEntity(
                            /*id = 2,*/
                            type = "Recolha",
                            creation_date= 1759021800000L,  // 28-09-2025 08:30 em millis
                            deadline = 1759023600000L,       // 28-09-2025 09:00 em millis
                            time_window = "10:00-10:30",
                            description = "Caixa 5Kg",
                            /* done = false, */
                            status = "Não concluído",
                            pickup_location = "Largo Visconde Almeid Ganat",
                            delivery_location = "Quinta dos Prados, Chocas",
                            longitude = 41.2994,
                            latitude =-7.7496,
                            priority = Priority.MEDIUM,
                            client_id = 3)
                    )
                } catch (_: Exception) {}
            }
        }

    }



}