package ru.goodibunakov.iremember.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.Utils;
import ru.goodibunakov.iremember.alarm.AlarmHelper;
import ru.goodibunakov.iremember.model.ModelTask;

public class AddingTaskDialogFragment extends DialogFragment {

    private Unbinder unbinder;
    private AddingTaskListener addingTaskListener;

    @BindView(R.id.dialogTaskTitle)
    TextInputLayout tilTitle;
    @BindView(R.id.dialogTaskDate)
    TextInputLayout tilDate;
    @BindView(R.id.dialogTaskTime)
    TextInputLayout tilTime;
    @BindView(R.id.date)
    EditText date;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.time)
    EditText time;
    @BindView(R.id.spinner_priority)
    Spinner spinner;

    public interface AddingTaskListener {
        void onTaskAdded(ModelTask newTask);

        void onTaskAddingCancel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            addingTaskListener = (AddingTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AddingTaskListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View container = inflater.inflate(R.layout.dialog_task, null);
        unbinder = ButterKnife.bind(this, container);

        builder.setTitle(R.string.dialog_title);
        builder.setIcon(R.mipmap.ic_launcher);
        tilTitle.setHint(getResources().getString(R.string.task_title));
        tilDate.setHint(getResources().getString(R.string.task_date));
        tilTime.setHint(getResources().getString(R.string.task_time));

        builder.setView(container);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);

        ModelTask modelTask = new ModelTask();

        ArrayAdapter<String> priorityAdapter;
        if (getResources().getConfiguration().locale.getCountry().equals("RU")){
            priorityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS_RU);
        } else {
            priorityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, ModelTask.PRIORITY_LEVELS);
        }
        spinner.setAdapter(priorityAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelTask.setPriority(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                modelTask.setPriority(0);
            }
        });

        date.setOnClickListener(v -> {
            if (date.length() == 0) {
                date.setText("");
            }

            DatePickerController datePickerController = new DatePickerController() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    date.setText(Utils.getDate(calendar.getTimeInMillis()));
                }
            };
            datePickerController.onCreateDialog(getActivity()).show();
        });

        time.setOnClickListener(v -> {

            if (time.length() == 0) {
                time.setText("");
            }

            TimePickerController timePickerController = new TimePickerController() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    time.setText(Utils.getTime(calendar.getTimeInMillis()));
                }
            };
            timePickerController.onCreateDialog(getActivity()).show();
        });

        builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
            modelTask.setTitle(title.getText().toString());
            if (date.length() != 0 || time.length() != 0){
                modelTask.setDate(calendar.getTimeInMillis());
                AlarmHelper alarmHelper = AlarmHelper.getInstance();
                alarmHelper.setAlarm(modelTask);
            }
            modelTask.setStatus(ModelTask.STATUS_CURRENT);
            addingTaskListener.onTaskAdded(modelTask);
            dialog.dismiss();
            Log.d("debug", "model = " + modelTask.toString(modelTask));
        });
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            addingTaskListener.onTaskAddingCancel();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
//        Objects.requireNonNull(alertDialog.getWindow()).setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.setOnShowListener(dialog -> {
            Button positive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
            if (title.length() == 0) {
                positive.setEnabled(false);
                tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
            }
            title.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        positive.setEnabled(false);
                        tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                    } else {
                        positive.setEnabled(true);
                        tilTitle.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        });
        return alertDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}