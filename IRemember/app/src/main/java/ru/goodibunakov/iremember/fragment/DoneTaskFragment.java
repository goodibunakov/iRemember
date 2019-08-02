package ru.goodibunakov.iremember.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.adapter.DoneTaskAdapter;
import ru.goodibunakov.iremember.database.DbHelper;
import ru.goodibunakov.iremember.model.ModelTask;


public class DoneTaskFragment extends TaskFragment {

    @BindView(R.id.rv)
    RecyclerView recyclerView;

    private Unbinder unbinder;
    private OnTaskRestoreListener onTaskRestoreListener;

    public DoneTaskFragment() {
        adapter = new DoneTaskAdapter(this);
    }

    public interface OnTaskRestoreListener {
        void onTaskRestore(ModelTask modelTask);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onTaskRestoreListener = (OnTaskRestoreListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTaskRestoreListener");
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDb) {
        int position = -1;
        if (adapter.getItemCount() > 0) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (adapter.getItem(i).isTask()) {
                    ModelTask task = (ModelTask) adapter.getItem(i);
                    if (newTask.getDate() < task.getDate()) {
                        position = i;
                        break;
                    }
                }
            }
        }

        if (position != -1) {
            adapter.addItem(position, newTask);
        } else {
            adapter.addItem(newTask);
        }

        if (saveToDb) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_done_task, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void moveTask(ModelTask modelTask) {
        if (modelTask.getDate() != 0) {
            alarmHelper.setAlarm(modelTask);
        }
        onTaskRestoreListener.onTaskRestore(modelTask);
    }

    @Override
    public void addTaskFromDb() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>(activity.dbHelper.query().getTasks(DbHelper.SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_DONE)},
                DbHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        if (activity != null && activity.dbHelper != null) {
            List<ModelTask> tasks = new ArrayList<>(activity.dbHelper.query().getTasks(DbHelper.SELECTION_LIKE_TITLE + " AND "
                            + DbHelper.SELECTION_STATUS,
                    new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_DONE)},
                    DbHelper.TASK_DATE_COLUMN));
            for (int i = 0; i < tasks.size(); i++) {
                addTask(tasks.get(i), false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
