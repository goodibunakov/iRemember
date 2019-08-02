package ru.goodibunakov.iremember.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import ru.goodibunakov.iremember.database.DbHelper;
import ru.goodibunakov.iremember.model.ModelTask;

public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DbHelper dbHelper = new DbHelper(context);

        AlarmHelper.getInstance().init();
        AlarmHelper alarmHelper = AlarmHelper.getInstance();
        List<ModelTask> tasks = new ArrayList<>(dbHelper.query().getTasks(DbHelper.SELECTION_STATUS + " OR "
                        + DbHelper.SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)},
                DbHelper.TASK_DATE_COLUMN));

        for (ModelTask task: tasks){
            if (task.getDate() != 0){
                alarmHelper.setAlarm(task);
            }
        }
    }
}
