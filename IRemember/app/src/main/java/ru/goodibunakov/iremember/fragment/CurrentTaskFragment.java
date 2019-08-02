package ru.goodibunakov.iremember.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.adapter.CurrentTasksAdapter;
import ru.goodibunakov.iremember.database.DbHelper;
import ru.goodibunakov.iremember.dialog.AddingTaskDialogFragment;
import ru.goodibunakov.iremember.model.ModelSeparator;
import ru.goodibunakov.iremember.model.ModelTask;


public class CurrentTaskFragment extends TaskFragment {

    private Unbinder unbinder;
    private OnTaskDoneListener onTaskDoneListener;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rv)
    RecyclerView recyclerView;

    public CurrentTaskFragment() {
        adapter = new CurrentTasksAdapter(this);
    }

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask modelTask);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onTaskDoneListener = (OnTaskDoneListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnTaskDoneListener");
        }
    }

    @Override
    public void addTask(ModelTask newTask, boolean saveToDb) {
        int position = -1;
        ModelSeparator separator = null;

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

        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);
                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.setDateStatus(ModelSeparator.TYPE_TODAY);
                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);
                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.setDateStatus(ModelSeparator.TYPE_FUTURE);
                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }
        }

        if (position != -1) {
            if (!adapter.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && adapter.getItem(position - 2).isTask()) {
                    ModelTask task = (ModelTask) adapter.getItem(position - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        position -= 1;
                    }
                } else if (position - 2 < 0 && newTask.getDate() == 0) {
                    position -= 1;
                }
            }

            if (separator != null){
                adapter.addItem(position-1, separator);
            }

            adapter.addItem(position, newTask);
        } else {
            if (separator != null){
                adapter.addItem(separator);
            }
            adapter.addItem(newTask);
        }

        if (saveToDb) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_task, container, false);
        unbinder = ButterKnife.bind(this, view);
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @OnClick(R.id.fab)
    void onClick() {
        DialogFragment addingTaskDialogFragment = new AddingTaskDialogFragment();
        if (Objects.requireNonNull(getActivity()).getSupportFragmentManager() != null) {
            addingTaskDialogFragment.show(getActivity().getSupportFragmentManager(), "AddingTaskDialogFragment");
        }
    }

    @Override
    public void moveTask(ModelTask modelTask) {
        alarmHelper.removeAlarm(modelTask.getTimestamp());
        onTaskDoneListener.onTaskDone(modelTask);
    }

    @Override
    public void addTaskFromDb() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>(activity.dbHelper.query()
                .getTasks(DbHelper.SELECTION_STATUS + " OR " + DbHelper.SELECTION_STATUS,
                        new String[]{Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)},
                        DbHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        Log.d("debug", "activity findTasks CurrentTaskFragment = " + activity);
        if (activity != null && activity.dbHelper != null) {
            List<ModelTask> tasks = new ArrayList<>(activity.dbHelper.query().getTasks(DbHelper.SELECTION_LIKE_TITLE + " AND "
                            + DbHelper.SELECTION_STATUS + " OR " + DbHelper.SELECTION_STATUS,
                    new String[]{"%" + title + "%", Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)},
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