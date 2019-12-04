package ru.goodibunakov.iremember.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.model.ModelSeparator
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment
import java.util.*

class DoneTasksAdapter(taskFragment: DoneTaskFragment) : TasksAdapter(taskFragment) {

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