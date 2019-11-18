package ru.goodibunakov.iremember.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.model.ModelSeparator
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragment

class CurrentTasksAdapter(taskFragment: CurrentTaskFragment) : TaskAdapter(taskFragment) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_TASK) {
            val layoutInflater = LayoutInflater.from(parent.context)
            val itemView = layoutInflater.inflate(R.layout.item_task, parent, false)
            return TaskViewHolderCurrent(itemView)
        }
        val separator = LayoutInflater.from(parent.context).inflate(R.layout.separator, parent, false)
        val type = separator.findViewById<TextView>(R.id.separator)
        return SeparatorViewHolder(separator, type)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items!![position]

        if (item.isTask()) {
            (holder as TaskViewHolderCurrent).bind(item)
            setAnimation(holder.itemView, position)
        } else {
            val separator = item as ModelSeparator
            val separatorViewHolder = holder as SeparatorViewHolder
            separatorViewHolder.type.text = holder.itemView.resources.getString(separator.getType())
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isTask()) {
            TYPE_TASK
        } else {
            TYPE_SEPARATOR
        }
    }

    companion object {
        private const val TYPE_TASK = 0
        private const val TYPE_SEPARATOR = 1
    }
}