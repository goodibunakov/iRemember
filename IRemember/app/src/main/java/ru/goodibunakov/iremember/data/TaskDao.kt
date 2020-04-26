package ru.goodibunakov.iremember.data

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Observable
import ru.goodibunakov.iremember.data.DatabaseConstants.TASKS_TABLE
import ru.goodibunakov.iremember.data.DatabaseConstants.TASK_DATE_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.TASK_STATUS_COLUMN
import ru.goodibunakov.iremember.data.DatabaseConstants.TASK_TIMESTAMP_COLUMN
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_CURRENT
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_DONE
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_OVERDUE

@Dao
interface TaskDao {

    @Query("SELECT * FROM $TASKS_TABLE WHERE $TASK_STATUS_COLUMN = $STATUS_CURRENT OR $TASK_STATUS_COLUMN = $STATUS_OVERDUE ORDER BY $TASK_DATE_COLUMN DESC")
    fun findCurrentTasks(): Observable<List<Task>>

    @Query("SELECT * FROM $TASKS_TABLE WHERE $TASK_STATUS_COLUMN = $STATUS_DONE ORDER BY $TASK_DATE_COLUMN DESC")
    fun findDoneTasks(): Observable<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Query("DELETE FROM $TASKS_TABLE WHERE $TASK_TIMESTAMP_COLUMN = :timestamp")
    fun delete(timestamp: Long): Completable

    @Query("DELETE FROM $TASKS_TABLE WHERE $TASK_STATUS_COLUMN = $STATUS_DONE")
    fun deleteAllDoneTasks(): Completable
}