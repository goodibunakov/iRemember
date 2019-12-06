package ru.goodibunakov.iremember.data

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Observable
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_CURRENT
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_DONE
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_OVERDUE
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASKS_TABLE
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_DATE_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_STATUS_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_TIMESTAMP_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.Companion.TASK_TITLE_COLUMN

@Dao
interface TaskDao {

    @Query("SELECT * FROM $TASKS_TABLE WHERE $TASK_TITLE_COLUMN LIKE '%' || :queryTitle || '%' AND $TASK_STATUS_COLUMN = $STATUS_CURRENT OR $TASK_STATUS_COLUMN = $STATUS_OVERDUE ORDER BY $TASK_DATE_COLUMN DESC")
    fun findCurrentTasks(queryTitle: String): Observable<List<Task>>

    @Query("SELECT * FROM $TASKS_TABLE WHERE $TASK_TITLE_COLUMN LIKE '%' || :queryTitle || '%' AND $TASK_STATUS_COLUMN = $STATUS_DONE ORDER BY $TASK_DATE_COLUMN DESC")
    fun findDoneTasks(queryTitle: String): Observable<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Query("DELETE FROM $TASKS_TABLE WHERE $TASK_TIMESTAMP_COLUMN = :timestamp")
    fun delete(timestamp: Long): Completable
}