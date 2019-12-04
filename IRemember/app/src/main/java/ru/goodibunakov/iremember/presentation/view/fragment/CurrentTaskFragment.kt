package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_current_task.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.data.DbHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.CurrentTaskFragmentPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.CurrentTasksAdapter
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment
import java.util.*

class CurrentTaskFragment : TaskFragment(), CurrentTaskFragmentView {

    private var onTaskDoneListener: OnTaskDoneListener? = null

    @InjectPresenter
    lateinit var currentTaskFragmentPresenter: CurrentTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): CurrentTaskFragmentPresenter {
        return CurrentTaskFragmentPresenter(RememberApp.getBus())
    }

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

    override fun showError(s: Int) {
        Toast.makeText(context, getText(s), Toast.LENGTH_SHORT).show()
    }

    override fun addTask(newTask: ModelTask) {
        adapter?.addTask(newTask)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_current_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        fab.setOnClickListener {
            currentTaskFragmentPresenter.showViewToAddTask()
        }
    }

    override fun showAddingTaskDialog() {
        val addingTaskDialogFragment = AddingTaskDialogFragment()
        addingTaskDialogFragment.show(activity!!.supportFragmentManager, "AddingTaskDialogFragment")
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && fab.isShown) {
                    currentTaskFragmentPresenter.hideFab()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentTaskFragmentPresenter.showFab()
                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun hideFab() {
        fab.hide()
    }

    override fun showFab() {
        fab.show()
    }

    override fun moveTask(modelTask: ModelTask) {
        alarmHelper?.removeAlarm(modelTask.timestamp)
        onTaskDoneListener?.onTaskDone(modelTask)
    }

//    override fun findTasks(title: String) {
//        checkAdapter()
//        adapter!!.removeAllItems()
//        if (activity != null && activity!!.dbHelper != null) {
//            val tasks = ArrayList(activity!!.dbHelper!!.query().getTasks(DbHelper.SELECTION_LIKE_TITLE + " AND "
//                    + DbHelper.SELECTION_STATUS + " OR " + DbHelper.SELECTION_STATUS,
//                    arrayOf("%$title%", ModelTask.STATUS_CURRENT.toString(), ModelTask.STATUS_OVERDUE.toString()),
//                    DbHelper.TASK_DATE_COLUMN))
//            for (i in tasks.indices) {
//                addTask(tasks[i], false)
//            }
//        }
//    }

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = CurrentTasksAdapter(this)
        }
    }
}