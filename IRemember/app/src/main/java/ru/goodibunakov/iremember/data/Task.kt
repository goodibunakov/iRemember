package ru.goodibunakov.iremember.data

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASKS_TABLE
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_DATE_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_PRIORITY_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_STATUS_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_TIMESTAMP_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_TITLE_COLUMN

@Entity(tableName = "tasks_table")
data class Task(
//        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = BaseColumns._ID) val id: Long,
                @ColumnInfo(name = "task_title") val title: String,
                @ColumnInfo(name = "task_date") val date: Long,
                @ColumnInfo(name = "task_priority") val priority: Int,
                @ColumnInfo(name = "task_status") val status: Int,
                @PrimaryKey @ColumnInfo(name = "task_timestamp") val timestamp: Long)

//const val TASKS_TABLE = "tasks_table"
//const val TASK_TITLE_COLUMN = "task_title"
//const val TASK_DATE_COLUMN = "task_date"
//const val TASK_PRIORITY_COLUMN = "task_priority"
//const val TASK_STATUS_COLUMN = "task_status"
//const val TASK_TIMESTAMP_COLUMN = "task_timestamp"