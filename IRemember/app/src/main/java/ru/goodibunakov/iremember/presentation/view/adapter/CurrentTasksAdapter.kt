package ru.goodibunakov.iremember.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.goodibunakov.iremember.databinding.ItemTaskBinding
import ru.goodibunakov.iremember.databinding.SeparatorBinding
import ru.goodibunakov.iremember.presentation.OnItemClickListener
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.ModelSeparator
import ru.goodibunakov.iremember.presentation.model.ModelTask
import java.util.*

class CurrentTasksAdapter(
    private val onClickListener: OnItemClickListener,
    private val onLongClickListener: OnItemLongClickListener,
    private val onPriorityClickListener: OnPriorityClickListener
) : TasksAdapter() {

    private val calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_TASK) {
            val binding = ItemTaskBinding.inflate(layoutInflater, parent, false)
            return TaskViewHolderCurrent(
                binding,
                onClickListener,
                onLongClickListener,
                onPriorityClickListener
            )
        }
        val separator = SeparatorBinding.inflate(layoutInflater, parent, false)
        return SeparatorViewHolder(separator, separator.separator)
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

    override fun addTask(newTask: ModelTask) {
        var position = -1
        var separator: ModelSeparator? = null

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

        if (newTask.date != 0L) {
            calendar.timeInMillis = newTask.date

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance()
                    .get(Calendar.DAY_OF_YEAR)
                || calendar.get(Calendar.YEAR) < Calendar.getInstance().get(Calendar.YEAR)
            ) {
                newTask.dateStatus = ModelSeparator.TYPE_OVERDUE
                if (!containsSeparatorOverdue) {
                    containsSeparatorOverdue = true
                    separator = ModelSeparator(ModelSeparator.TYPE_OVERDUE)
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance()
                    .get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
            ) {
                newTask.dateStatus = ModelSeparator.TYPE_TODAY
                if (!containsSeparatorToday) {
                    containsSeparatorToday = true
                    separator = ModelSeparator(ModelSeparator.TYPE_TODAY)
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance()
                    .get(Calendar.DAY_OF_YEAR) + 1
                && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
            ) {
                newTask.dateStatus = ModelSeparator.TYPE_TOMORROW
                if (!containsSeparatorTomorrow) {
                    containsSeparatorTomorrow = true
                    separator = ModelSeparator(ModelSeparator.TYPE_TOMORROW)
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance()
                    .get(Calendar.DAY_OF_YEAR) + 1
                || calendar.get(Calendar.YEAR) > Calendar.getInstance().get(Calendar.YEAR)
            ) {
                newTask.dateStatus = ModelSeparator.TYPE_FUTURE
                if (!containsSeparatorFuture) {
                    containsSeparatorFuture = true
                    separator = ModelSeparator(ModelSeparator.TYPE_FUTURE)
                }
            }
        }


        if (position != -1) {
            if (!getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && getItem(position - 2).isTask()) {
                    val task = getItem(position - 2) as ModelTask
                    if (task.dateStatus == newTask.dateStatus) {
                        position -= 1
                    }
                } else if (position - 2 < 0 && newTask.date == 0L) {
                    position -= 1
                }
            }

            if (separator != null) {
                addItem(position - 1, separator)
            }
            addItem(position, newTask)
        } else {
            if (separator != null) {
                addItem(separator)
            }
            addItem(newTask)
        }
    }

    companion object {
        private const val TYPE_TASK = 0
        private const val TYPE_SEPARATOR = 1
    }
}