package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_done_task.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.data.DbHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.DoneTaskFragmentPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.DoneTasksAdapter

class DoneTaskFragment : TaskFragment(), DoneTaskFragmentView {

    @InjectPresenter
    lateinit var doneTaskFragmentPresenter: DoneTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): DoneTaskFragmentPresenter {
        return DoneTaskFragmentPresenter()
    }

    init {
        adapter = DoneTasksAdapter(this)
    }

    private var onTaskRestoreListener: OnTaskRestoreListener? = null

    interface OnTaskRestoreListener {
        fun onTaskRestore(modelTask: ModelTask)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onTaskRestoreListener = context as OnTaskRestoreListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnTaskRestoreListener")
        }
    }

    override fun addTask(newTask: ModelTask) {
//        var position = -1
//        checkAdapter()
//        if (adapter!!.itemCount > 0) {
//            for (i in 0 until adapter!!.itemCount) {
//                if (adapter!!.getItem(i).isTask()) {
//                    val task = adapter!!.getItem(i) as ModelTask
//                    if (newTask.date < task.date) {
//                        position = i
//                        break
//                    }
//                }
//            }
//        }
//
//        if (position != -1) {
//            adapter!!.addItem(position, newTask)
//        } else {
//            adapter!!.addItem(newTask)
//        }
//
//        if (saveToDb) {
//            activity?.dbHelper!!.saveTask(newTask)
//        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_done_task, container, false)
    }

    override fun showError(s: Int) {
//        Toast.makeText(context, getText(s), Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    override fun moveTask(modelTask: ModelTask) {
        if (modelTask.date != 0L) {
            alarmHelper?.setAlarm(modelTask)
        }
        onTaskRestoreListener?.onTaskRestore(modelTask)
    }

    fun findDoneTasks(title: String) {
//        checkAdapter()
//        adapter?.removeAllItems()
//        if (activity != null && activity!!.dbHelper != null) {
//            val tasks = ArrayList(activity!!.dbHelper!!.query().getTasks(DbHelper.SELECTION_LIKE_TITLE + " AND "
//                    + DbHelper.SELECTION_STATUS,
//                    arrayOf("%$title%", ModelTask.STATUS_DONE.toString()),
//                    DbHelper.TASK_DATE_COLUMN))
//            for (i in tasks.indices) {
//                addTask(tasks[i], false)
//            }
//        }
    }

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = DoneTasksAdapter(this)
        }
    }
}