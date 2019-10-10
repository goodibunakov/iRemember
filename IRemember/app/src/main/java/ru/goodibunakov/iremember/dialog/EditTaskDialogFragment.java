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

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.alarm.AlarmHelper;
import ru.goodibunakov.iremember.model.ModelTask;
import ru.goodibunakov.iremember.utils.Utils;

public class EditTaskDialogFragment extends DialogFragment {

    private EditingTaskListener editingTaskListener;
    private Unbinder unbinder;

    @BindView(R.id.dialogTaskTitle)
    TextInputLayout tilTitle;
    @BindView(R.id.dialogTaskDate)
    TextInputLayout tilDate;
    @BindView(R.id.dialogTaskTime)
    TextInputLayout tilTime;
    @BindView(R.id.date)
    EditText etDate;
    @BindView(R.id.title)
    EditText etTitle;
    @BindView(R.id.time)
    EditText time;
    @BindView(R.id.spinner_priority)
    Spinner spinner;

    public static EditTaskDialogFragment newInstance(ModelTask task) {
        EditTaskDialogFragment editTaskDialogFragment = new EditTaskDialogFragment();

        Bundle args = new Bundle();
        args.putString("title", task.getTitle());
        args.putLong("date", task.getDate());
        args.putInt("priority", task.getPriority());
        args.putLong("timestamp", task.getTimestamp());

        editTaskDialogFragment.setArguments(args);
        return editTaskDialogFragment;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            editingTaskListener = (EditingTaskListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implements EditingTaskListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        String title;
        long date, timestamp;
        int priority;
        ModelTask modelTask;

        if (args != null) {
            title = args.getString("title");
            date = args.getLong("date");
            priority = args.getInt("priority");
            timestamp = args.getLong("timestamp");
            modelTask = new ModelTask(title, date, priority, 0, timestamp);


            final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            LayoutInflater inflater = getActivity().getLayoutInflater();
            @SuppressLint("InflateParams") View container = inflater.inflate(R.layout.dialog_task, null);
            unbinder = ButterKnife.bind(this, container);

            builder.setTitle(R.string.editing_title);
            builder.setIcon(R.mipmap.ic_launcher);
            etTitle.setText(title);
            etTitle.setSelection(etTitle.length());
            if (date != 0) {
                etDate.setText(Utils.getDate(date));
                time.setText(Utils.getTime(timestamp));
            }

            tilTitle.setHint(getResources().getString(R.string.task_title));
            tilDate.setHint(getResources().getString(R.string.task_date));
            tilTime.setHint(getResources().getString(R.string.task_time));

            builder.setView(container);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);

            ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, getActivity().getResources().getStringArray(R.array.priority_array));

            spinner.setAdapter(priorityAdapter);
            spinner.setSelection(priority);
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

            if (etDate.length() != 0 || time.length() != 0){
                calendar.setTimeInMillis(date);
            }

            etDate.setOnClickListener(v -> {
                if (etDate.length() == 0) {
                    etDate.setText("");
                }

                DatePickerController datePickerController = new DatePickerController() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etDate.setText(Utils.getDate(calendar.getTimeInMillis()));
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
                modelTask.setTitle(etTitle.getText().toString());
                if (etDate.length() != 0 || time.length() != 0) {
                    modelTask.setDate(calendar.getTimeInMillis());
                    AlarmHelper alarmHelper = AlarmHelper.getInstance();
                    alarmHelper.setAlarm(modelTask);
                }
                modelTask.setStatus(ModelTask.STATUS_CURRENT);
                editingTaskListener.onTaskEdited(modelTask);
                dialog.dismiss();
                Log.d("debug", "model = " + modelTask.toString(modelTask));
            });
            builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(dialog -> {
                Button positive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                if (etTitle.length() == 0) {
                    positive.setEnabled(false);
                    tilTitle.setError(getResources().getString(R.string.dialog_error_empty_title));
                }
                etTitle.addTextChangedListener(new TextWatcher() {
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
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface EditingTaskListener {
        void onTaskEdited(ModelTask updatedTask);
    }
}
