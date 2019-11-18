package ru.goodibunakov.iremember.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment

class DoneTaskAdapter(taskFragment: DoneTaskFragment) : TaskAdapter(taskFragment) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolderDone(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items!![position]

        if (item.isTask()) {
            (holder as TaskViewHolderDone).bind(item)
            setAnimation(holder.itemView, position)
        }
    }
}