package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
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
import ru.goodibunakov.iremember.presentation.OnItemClickListener
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.CurrentTaskFragmentPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.CurrentTasksAdapter
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment
import ru.goodibunakov.iremember.presentation.view.dialog.EditTaskDialogFragment

class CurrentTaskFragment : TaskFragment(), CurrentTaskFragmentView, OnItemClickListener, OnItemLongClickListener, OnPriorityClickListener {

    private var onTaskDoneListener: OnTaskDoneListener? = null

    @InjectPresenter
    lateinit var currentTaskFragmentPresenter: CurrentTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): CurrentTaskFragmentPresenter {
        return CurrentTaskFragmentPresenter(RememberApp.getBus())
    }

    init {
        adapter = CurrentTasksAdapter(this, this, this)
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

//    override fun addTask(newTask: ModelTask) {
//        adapter?.addTask(newTask)
//    }


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

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = CurrentTasksAdapter(this, this, this)
        }
    }

    override fun onItemClick(task: ModelTask) {
        currentTaskFragmentPresenter.onItemClick(task)
    }

    override fun showEditTaskDialog(task: ModelTask) {
        val editingDialog = EditTaskDialogFragment.newInstance(task)
        editingDialog.show(getActivity()!!.supportFragmentManager, "EditTaskDialogFragment")
    }

    override fun onItemLongClick(location: Int): Boolean {
        Handler().postDelayed({ currentTaskFragmentPresenter.onItemLongClick(location) }, 500)
        return true
    }

    override fun onPriorityClick(modelTask: ModelTask) {
        currentTaskFragmentPresenter.updateTask(modelTask)
    }
}