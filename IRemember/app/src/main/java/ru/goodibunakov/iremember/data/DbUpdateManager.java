package ru.goodibunakov.iremember.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import ru.goodibunakov.iremember.presentation.model.ModelTask;

public class DbUpdateManager {

    private final SQLiteDatabase database;

    DbUpdateManager(SQLiteDatabase database) {
        this.database = database;
    }

    private void title(long timestamp, String title) {
        update(timestamp, title);
    }

    private void date(long timestamp, long date) {
        update(DbHelper.TASK_DATE_COLUMN, timestamp, date);
    }

    private void priority(long timestamp, int priority) {
        update(DbHelper.TASK_PRIORITY_COLUMN, timestamp, priority);
    }

    public void status(long timestamp, int status) {
        update(DbHelper.TASK_STATUS_COLUMN, timestamp, status);
    }

    public void task(ModelTask modelTask) {
        title(modelTask.getTimestamp(), modelTask.getTitle());
        date(modelTask.getTimestamp(), modelTask.getDate());
        priority(modelTask.getTimestamp(), modelTask.getPriority());
        status(modelTask.getTimestamp(), modelTask.getStatus());
    }

    private void update(long key, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.TASK_TITLE_COLUMN, value);
        database.update(DbHelper.TASKS_TABLE, contentValues, DbHelper.TASK_TIMESTAMP_COLUMN + " = " + key, null);
    }

    private void update(String column, long key, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        database.update(DbHelper.TASKS_TABLE, contentValues, DbHelper.TASK_TIMESTAMP_COLUMN + " = " + key, null);
    }
}
