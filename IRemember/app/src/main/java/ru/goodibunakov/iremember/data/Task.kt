package ru.goodibunakov.iremember.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASKS_TABLE
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_DATE_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_PRIORITY_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_STATUS_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_TIMESTAMP_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_TITLE_COLUMN

@Entity(tableName = TASKS_TABLE)
data class Task(@ColumnInfo(name = TASK_TITLE_COLUMN) val title: String,
                @ColumnInfo(name = TASK_DATE_COLUMN) val date: Long,
                @ColumnInfo(name = TASK_PRIORITY_COLUMN) val priority: Int,
                @ColumnInfo(name = TASK_STATUS_COLUMN) val status: Int,
                @PrimaryKey @ColumnInfo(name = TASK_TIMESTAMP_COLUMN) val timestamp: Long)