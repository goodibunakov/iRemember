package ru.goodibunakov.iremember.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.goodibunakov.iremember.data.DatabaseConstants.DATABASE_NAME
import ru.goodibunakov.iremember.data.DatabaseConstants.DATABASE_VERSION

@Database(entities = [Task::class], version = DATABASE_VERSION, exportSchema = true)
abstract class TasksDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: TasksDatabase? = null

        fun getDatabase(context: Context): TasksDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        TasksDatabase::class.java,
                        DATABASE_NAME).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}