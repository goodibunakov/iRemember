package ru.goodibunakov.iremember.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.goodibunakov.iremember.MainActivity;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.adapter.TaskAdapter;
import ru.goodibunakov.iremember.alarm.AlarmHelper;
import ru.goodibunakov.iremember.dialog.EditTaskDialogFragment;
import ru.goodibunakov.iremember.model.Item;
import ru.goodibunakov.iremember.model.ModelTask;

public abstract class TaskFragment extends Fragment {

    protected TaskAdapter adapter;
    public MainActivity activity;
    AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        addTaskFromDb();
//        Log.d("debug", "TaskFragment onActivityCreated");


        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }

        alarmHelper = AlarmHelper.getInstance();

        addTaskFromDb();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("debug", "TaskFragment onAttach");
//        activity = (MainActivity) context;
//        alarmHelper = AlarmHelper.getInstance();
//        Log.d("debug", "activity onAttach TaskFragment " + activity);
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDb);

    public abstract void moveTask(ModelTask modelTask);

    public abstract void addTaskFromDb();

    public abstract void findTasks(String title);

    public void removeTaskDialog(int location) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(R.string.dialog_remove_message);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        Item item = adapter.getItem(location);

        if (item.isTask()) {
            ModelTask removingTask = (ModelTask) item;
            long timestamp = removingTask.getTimestamp();

            builder.setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                adapter.removeItem(location);
                activity.dbHelper.removeTask(timestamp);
                Toast.makeText(activity, getResources().getString(R.string.removed), Toast.LENGTH_SHORT).show();
                alarmHelper.removeAlarm(timestamp);
                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.dialog_cancel, (dialog, which) -> dialog.cancel());
        }
        builder.show();
    }

    public void updateTask(ModelTask task) {
        adapter.updateTask(task);
    }

    public void showTaskEditDialog(ModelTask task){
        DialogFragment editingDialog = EditTaskDialogFragment.newInstance(task);
        editingDialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "EditTaskDialogFragment");
    }
}