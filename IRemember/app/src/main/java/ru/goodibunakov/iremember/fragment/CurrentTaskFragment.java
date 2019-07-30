package ru.goodibunakov.iremember.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
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
import ru.goodibunakov.iremember.model.ModelTask;


public class CurrentTaskFragment extends TaskFragment {

    private Unbinder unbinder;
    private OnTaskDoneListener onTaskDoneListener;

    @BindView(R.id.fab)
    FloatingActionButton fab;
//    @BindView(R.id.rv)
//    RecyclerView recyclerView;

    public CurrentTaskFragment() {
        // Required empty public constructor
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_task, container, false);
        unbinder = ButterKnife.bind(this, view);
        recyclerView = view.findViewById(R.id.rv);
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        adapter = new CurrentTasksAdapter(this);
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
        onTaskDoneListener.onTaskDone(modelTask);
    }

    @Override
    public void addTaskFromDb() {
        List<ModelTask> tasks = new ArrayList<>(activity.dbHelper.query().getTasks(DbHelper.SELECTION_STATUS + " OR "
                        + DbHelper.SELECTION_STATUS,
                new String[]{Integer.toString(ModelTask.STATUS_CURRENT), Integer.toString(ModelTask.STATUS_OVERDUE)},
                DbHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}