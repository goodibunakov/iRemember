package ru.goodibunakov.iremember.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import ru.goodibunakov.iremember.RememberApp;
import ru.goodibunakov.iremember.model.ModelTask;

public class AlarmHelper {

    private static AlarmHelper instance;
    private AlarmManager alarmManager;

    public static AlarmHelper getInstance() {
        if (instance == null) {
            instance = new AlarmHelper();
        }
        return instance;
    }

    private AlarmHelper() {
    }

    public void init() {
        alarmManager = (AlarmManager) RememberApp.getAppContext().getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(ModelTask task) {
        Intent intent = new Intent(RememberApp.getAppContext(), AlarmReceiver.class);
        intent.putExtra("title", task.getTitle());
        intent.putExtra("timestamp", task.getTimestamp());
        intent.putExtra("color", task.getPriorityColor());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(RememberApp.getAppContext(),
                (int) task.getTimestamp(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, task.getDate(), pendingIntent);
    }

    public void removeAlarm(long timestamp) {
        Intent intent = new Intent(RememberApp.getAppContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(RememberApp.getAppContext(), (int) timestamp, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}