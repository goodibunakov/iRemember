package ru.goodibunakov.iremember.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import ru.goodibunakov.iremember.model.ModelTask;

public class DbUpdateManager {

    private SQLiteDatabase database;

    public DbUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }

    public void title(long timestamp, String title){
        update(DbHelper.TASK_TITLE_COLUMN, timestamp, title);
    }

    public void date(long timestamp, long date){
        update(DbHelper.TASK_DATE_COLUMN, timestamp, date);
    }

    public void priority(long timestamp, int priority){
        update(DbHelper.TASK_PRIORITY_COLUMN, timestamp, priority);
    }

    public void status(long timestamp, int status){
        update(DbHelper.TASK_STATUS_COLUMN, timestamp, status);
    }

    public void task(ModelTask modelTask){
        title(modelTask.getTimestamp(), modelTask.getTitle());
        date(modelTask.getTimestamp(), modelTask.getDate());
        priority(modelTask.getTimestamp(), modelTask.getPriority());
        status(modelTask.getTimestamp(), modelTask.getStatus());
    }

    private void update(String column, long key, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        database.update(DbHelper.TASKS_TABLE, contentValues, DbHelper.TASK_TIMESTAMP_COLUMN + " = " + key, null);
    }

    private void update(String column, long key, long value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        database.update(DbHelper.TASKS_TABLE, contentValues, DbHelper.TASK_TIMESTAMP_COLUMN + " = " + key, null);
    }
}
