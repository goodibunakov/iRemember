package ru.goodibunakov.iremember.data

import android.provider.BaseColumns

class DatabaseConstants {

    companion object {

        const val DATABASE_NAME = "iremember_database.db"
        const val DATABASE_VERSION = 1

        const val TASKS_TABLE = "tasks_table"
        const val TASK_TITLE_COLUMN = "task_title"
        const val TASK_DATE_COLUMN = "task_date"
        const val TASK_PRIORITY_COLUMN = "task_priority"
        const val TASK_STATUS_COLUMN = "task_status"
        const val TASK_TIMESTAMP_COLUMN = "task_timestamp"

//        private const val TASKS_TABLE_CREATE_SCRIPT : String = ("CREATE TABLE "
//                + DbHelper.TASKS_TABLE + " (" + BaseColumns._ID
//                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DbHelper.TASK_TITLE_COLUMN + " TEXT NOT NULL, "
//                + DbHelper.TASK_DATE_COLUMN + " LONG, " + DbHelper.TASK_PRIORITY_COLUMN + " INTEGER, "
//                + DbHelper.TASK_STATUS_COLUMN + " INTEGER, " + DbHelper.TASK_TIMESTAMP_COLUMN + " LONG);")

    }
}