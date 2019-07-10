package ru.goodibunakov.iremember.fragment;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.adapter.TaskAdapter;
import ru.goodibunakov.iremember.model.ModelTask;

public abstract class TaskFragment extends Fragment {

    @BindView(R.id.rv)
    RecyclerView recyclerView;

    protected TaskAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    public void addTask(ModelTask newTask) {
//        if (adapter != null) {
            Log.d("debug", "adapter = " + adapter);
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
//        } else  {
//            Log.d("debug", "adapter in taskFragment = null!!!!");
//        }
    }

    public abstract void moveTask(ModelTask modelTask);
}
