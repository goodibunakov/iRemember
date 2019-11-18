package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_current_task.*
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.data.DbHelper
import ru.goodibunakov.iremember.presentation.model.ModelSeparator
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.adapter.CurrentTasksAdapter
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment
import java.util.*

class CurrentTaskFragment : TaskFragment() {

    private var onTaskDoneListener: OnTaskDoneListener? = null

    init {
        adapter = CurrentTasksAdapter(this)
    }

    interface OnTaskDoneListener {
        fun onTaskDone(modelTask: ModelTask)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onTaskDoneListener = context as OnTaskDoneListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnTaskDoneListener")
        }
    }

    override fun addTask(newTask: ModelTask, saveToDb: Boolean) {
        var position = -1
        var separator: ModelSeparator? = null

        checkAdapter()
        if (adapter!!.itemCount > 0) {
            for (i in 0 until adapter!!.itemCount) {
                if (adapter!!.getItem(i).isTask()) {
                    val task = adapter!!.getItem(i) as ModelTask
                    if (newTask.date < task.date) {
                        position = i
                        break
                    }
                }
            }
        }

        if (newTask.date != 0L) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = newTask.date

            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.dateStatus = ModelSeparator.TYPE_OVERDUE
                if (!adapter!!.containsSeparatorOverdue) {
                    adapter!!.containsSeparatorOverdue = true
                    separator = ModelSeparator(ModelSeparator.TYPE_OVERDUE)
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                newTask.dateStatus = ModelSeparator.TYPE_TODAY
                if (!adapter!!.containsSeparatorToday) {
                    adapter!!.containsSeparatorToday = true
                    separator = ModelSeparator(ModelSeparator.TYPE_TODAY)
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.dateStatus = ModelSeparator.TYPE_TOMORROW
                if (!adapter!!.containsSeparatorTomorrow) {
                    adapter!!.containsSeparatorTomorrow = true
                    separator = ModelSeparator(ModelSeparator.TYPE_TOMORROW)
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1) {
                newTask.dateStatus = ModelSeparator.TYPE_FUTURE
                if (!adapter!!.containsSeparatorFuture) {
                    adapter!!.containsSeparatorFuture = true
                    separator = ModelSeparator(ModelSeparator.TYPE_FUTURE)
                }
            }
        }

        if (position != -1) {
            if (!adapter!!.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && adapter!!.getItem(position - 2).isTask()) {
                    val task = adapter!!.getItem(position - 2) as ModelTask
                    if (task.dateStatus == newTask.dateStatus) {
                        position -= 1
                    }
                } else if (position - 2 < 0 && newTask.date == 0L) {
                    position -= 1
                }
            }

            if (separator != null) {
                adapter!!.addItem(position - 1, separator)
            }

            adapter!!.addItem(position, newTask)
        } else {
            if (separator != null) {
                adapter!!.addItem(separator)
            }
            adapter!!.addItem(newTask)
        }

        if (saveToDb) {
            activity?.dbHelper!!.saveTask(newTask)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_current_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        fab.setOnClickListener {
            val addingTaskDialogFragment = AddingTaskDialogFragment()
            addingTaskDialogFragment.show(activity!!.supportFragmentManager, "AddingTaskDialogFragment")
        }
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && fab.isShown) {
                    fab.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun moveTask(modelTask: ModelTask) {
        alarmHelper?.removeAlarm(modelTask.timestamp)
        onTaskDoneListener?.onTaskDone(modelTask)
    }

    override fun addTaskFromDb() {
        checkAdapter()
        adapter!!.removeAllItems()
        val tasks = ArrayList(activity!!.dbHelper!!.query()
                .getTasks(DbHelper.SELECTION_STATUS + " OR " + DbHelper.SELECTION_STATUS,
                        arrayOf(ModelTask.STATUS_CURRENT.toString(), ModelTask.STATUS_OVERDUE.toString()),
                        DbHelper.TASK_DATE_COLUMN))
        for (i in tasks.indices) {
            addTask(tasks[i], false)
        }
    }

    override fun findTasks(title: String) {
        checkAdapter()
        adapter!!.removeAllItems()
        Log.d("debug", "activity findTasks CurrentTaskFragment = $activity")
        if (activity != null && activity!!.dbHelper != null) {
            val tasks = ArrayList(activity!!.dbHelper!!.query().getTasks(DbHelper.SELECTION_LIKE_TITLE + " AND "
                    + DbHelper.SELECTION_STATUS + " OR " + DbHelper.SELECTION_STATUS,
                    arrayOf("%$title%", ModelTask.STATUS_CURRENT.toString(), ModelTask.STATUS_OVERDUE.toString()),
                    DbHelper.TASK_DATE_COLUMN))
            for (i in tasks.indices) {
                addTask(tasks[i], false)
            }
        }
    }

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = CurrentTasksAdapter(this)
            addTaskFromDb()
        }
    }
}