package ru.goodibunakov.iremember.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.goodibunakov.iremember.R;
import ru.goodibunakov.iremember.fragment.DoneTaskFragment;
import ru.goodibunakov.iremember.model.Item;

public class DoneTaskAdapter extends TaskAdapter {

    public DoneTaskAdapter(DoneTaskFragment taskFragment) {
        super(taskFragment);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View itemView = layoutInflater.inflate(R.layout.item_task, parent, false);
        return new TaskViewHolderDone(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);

        if (item.isTask()) {
            ((TaskViewHolderDone) holder).bind(item);
            setAnimation(holder.itemView, position);
            holder.itemView.setEnabled(true);
            holder.itemView.setVisibility(View.VISIBLE);
        }
    }
}