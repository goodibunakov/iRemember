package ru.goodibunakov.iremember.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.databinding.ItemTaskBinding
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.ModelTask

class DoneTasksAdapter(
    private val onLongClickListener: OnItemLongClickListener,
    private val onPriorityClickListener: OnPriorityClickListener
) : TasksAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)
        return TaskViewHolderDone(binding, onLongClickListener, onPriorityClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items!![position]

        if (item.isTask()) {
            (holder as TaskViewHolderDone).bind(item)
            setAnimation(holder.itemView, position)
        }
    }

    override fun addTask(newTask: ModelTask) {
        var position = -1

        if (itemCount > 0) {
            for (i in 0 until itemCount) {
                if (getItem(i).isTask()) {
                    val task = getItem(i) as ModelTask
                    if (newTask.date < task.date) {
                        position = i
                        break
                    }
                }
            }
        }

        if (position != -1) {
            addItem(position, newTask)
        } else {
            addItem(newTask)
        }
    }
}