package ru.goodibunakov.iremember.data

import androidx.room.*
import io.reactivex.Observable
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_CURRENT
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_DONE
import ru.goodibunakov.iremember.presentation.model.ModelTask.Companion.STATUS_OVERDUE

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks_table WHERE task_status = $STATUS_CURRENT OR task_status = $STATUS_OVERDUE ORDER BY task_date DESC")
    fun getCurrentTasks(): Observable<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE task_status = $STATUS_DONE ORDER BY task_date DESC")
    fun getDoneTasks(): Observable<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE task_title LIKE '%' || :queryTitle || '%' AND task_status = $STATUS_CURRENT OR task_status = $STATUS_OVERDUE ORDER BY task_date DESC")
    fun findCurrentTasks(queryTitle: String): Observable<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE task_title LIKE '%' || :queryTitle || '%' AND task_status = $STATUS_DONE ORDER BY task_date DESC")
    fun findDoneTasks(queryTitle: String): Observable<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)
}