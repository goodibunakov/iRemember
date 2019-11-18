package ru.goodibunakov.iremember.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.goodibunakov.iremember.presentation.model.ModelTask;

public class DbQueryManager {

    private final SQLiteDatabase database;

    DbQueryManager(SQLiteDatabase database) {
        this.database = database;
    }

    public List<ModelTask> getTasks(String selection, String[] selectionArgs, String orderBy) {
        List<ModelTask> tasks = new ArrayList<>();

        Cursor cursor = database.query(DbHelper.TASKS_TABLE, null, selection, selectionArgs, null, null, orderBy);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(DbHelper.TASK_TITLE_COLUMN));
                    long date = cursor.getLong(cursor.getColumnIndex(DbHelper.TASK_DATE_COLUMN));
                    long timestamp = cursor.getLong(cursor.getColumnIndex(DbHelper.TASK_TIMESTAMP_COLUMN));
                    int priority = cursor.getInt(cursor.getColumnIndex(DbHelper.TASK_PRIORITY_COLUMN));
                    int status = cursor.getInt(cursor.getColumnIndex(DbHelper.TASK_STATUS_COLUMN));
                    tasks.add(new ModelTask(title, date, priority, status, timestamp));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return tasks;
    }
}