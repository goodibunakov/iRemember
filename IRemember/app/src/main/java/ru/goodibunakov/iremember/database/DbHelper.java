package ru.goodibunakov.iremember.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import ru.goodibunakov.iremember.model.ModelTask;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "iremember_database";
    public static final String TASKS_TABLE = "tasks_table";
    public static final String TASK_TITLE_COLUMN = "task_title";
    public static final String TASK_DATE_COLUMN = "task_date";
    public static final String TASK_PRIORITY_COLUMN = "task_priority";
    public static final String TASK_STATUS_COLUMN = "task_status";
    public static final String TASK_TIMESTAMP_COLUMN = "task_timestamp";

    private static final String TASKS_TABLE_CREATE_SCRIPT = "CREATE TABLE "
            + TASKS_TABLE + " (" + BaseColumns._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_TITLE_COLUMN + " TEXT NOT NULL, "
            + TASK_DATE_COLUMN + " LONG, " + TASK_PRIORITY_COLUMN + " INTEGER, "
            + TASK_STATUS_COLUMN + " INTEGER, " + TASK_TIMESTAMP_COLUMN + " LONG);";

    public static final String SELECTION_STATUS = DbHelper.TASK_STATUS_COLUMN + "=?";
    public static final String SELECTION_TIMESTAMP = TASK_TIMESTAMP_COLUMN + "=?";
    public static final String SELECTION_LIKE_TITLE = TASK_TITLE_COLUMN + " LIKE ?";

    private DbQueryManager queryManager;
    private DbUpdateManager updateManager;


    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        queryManager = new DbQueryManager(getReadableDatabase());
        updateManager = new DbUpdateManager(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASKS_TABLE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TASKS_TABLE);
        onCreate(db);
    }

    public void saveTask(ModelTask modelTask) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_TITLE_COLUMN, modelTask.getTitle());
        contentValues.put(TASK_DATE_COLUMN, modelTask.getDate());
        contentValues.put(TASK_STATUS_COLUMN, modelTask.getStatus());
        contentValues.put(TASK_PRIORITY_COLUMN, modelTask.getPriority());
        contentValues.put(TASK_TIMESTAMP_COLUMN, modelTask.getTimestamp());

        getWritableDatabase().insert(TASKS_TABLE, null, contentValues);
    }

    public DbQueryManager query() {
        return queryManager;
    }

    public DbUpdateManager update() {
        return updateManager;
    }

    public void removeTask(long timestamp) {
        getWritableDatabase().delete(TASKS_TABLE, SELECTION_TIMESTAMP, new String[]{Long.toString(timestamp)});
    }
}