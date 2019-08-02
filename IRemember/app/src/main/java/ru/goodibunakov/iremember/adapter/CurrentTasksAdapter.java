package ru.goodibunakov.iremember.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.fragment.CurrentTaskFragment;
import ru.goodibunakov.iremember.model.Item;
import ru.goodibunakov.iremember.model.ModelSeparator;

public class CurrentTasksAdapter extends TaskAdapter {

    static final int TYPE_TASK = 0;
    static final int TYPE_SEPARATOR = 1;

    public CurrentTasksAdapter(CurrentTaskFragment taskFragment) {
        super(taskFragment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TASK:
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                final View itemView = layoutInflater.inflate(R.layout.item_task, parent, false);
                return new TaskViewHolderCurrent(itemView);
            case TYPE_SEPARATOR:
                View separator = LayoutInflater.from(parent.getContext()).inflate(R.layout.separator, parent, false);
                TextView type = separator.findViewById(R.id.separator);
                return new SeparatorViewHolder(separator, type);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);

        if (item.isTask()) {
            ((TaskViewHolderCurrent) holder).bind(item);
            setAnimation(holder.itemView, position);
        } else {
            ModelSeparator separator = (ModelSeparator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) holder;
            separatorViewHolder.type.setText(holder.itemView.getResources().getString(separator.getType()));
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else {
            return TYPE_SEPARATOR;
        }
    }
}
