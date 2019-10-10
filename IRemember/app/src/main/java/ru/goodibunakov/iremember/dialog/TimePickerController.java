package ru.goodibunakov.iremember.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.goodibunakov.iremember.R;

class TimePickerController implements TimePickerDialog.OnTimeSetListener {
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    }

    Dialog onCreateDialog(Context context) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(context, R.style.AppThemeDialog,this, hour, minute, DateFormat.is24HourFormat(context));
    }
}