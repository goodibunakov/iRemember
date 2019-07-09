package ru.goodibunakov.iremember.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

    public interface AddingTaskListener {
        void onTaskAdded();

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

        date.setOnClickListener(v -> {
            if (date.length() == 0) {
                date.setText("");
            }

            DatePickerController datePickerController = new DatePickerController() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar dateCalendar = Calendar.getInstance();
                    dateCalendar.set(year, month, dayOfMonth);
                    date.setText(Utils.getDate(dateCalendar.getTimeInMillis()));
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
                    Calendar timeCalendar = Calendar.getInstance();
                    timeCalendar.set(0, 0, 0, hourOfDay, minute);
                    time.setText(Utils.getTime(timeCalendar.getTimeInMillis()));
                }
            };
            timePickerController.onCreateDialog(getActivity()).show();
        });

        builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
            addingTaskListener.onTaskAdded();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
            addingTaskListener.onTaskAddingCancel();
            dialog.cancel();
        });

        AlertDialog alertDialog = builder.create();
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