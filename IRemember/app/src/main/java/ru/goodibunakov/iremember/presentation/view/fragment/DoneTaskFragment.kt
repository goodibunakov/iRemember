package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_done_task.*
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.DoneTaskFragmentPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.DoneTasksAdapter

class DoneTaskFragment : TaskFragment(), DoneTaskFragmentView, OnItemLongClickListener, OnPriorityClickListener {

    @InjectPresenter
    lateinit var doneTaskFragmentPresenter: DoneTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): DoneTaskFragmentPresenter {
        return DoneTaskFragmentPresenter(RememberApp.getBus())
    }

    init {
        adapter = DoneTasksAdapter(this, this)
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

//    override fun addTask(newTask: ModelTask) {
//        adapter?.addTask(newTask)
//    }

    override fun moveTask(modelTask: ModelTask) {
//        to delete
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_done_task, container, false)
    }

    override fun showError(s: Int) {
        Toast.makeText(context, getText(s), Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

//    override fun moveTask(modelTask: ModelTask) {
//        if (modelTask.date != 0L) {
//            alarmHelper?.setAlarm(modelTask)
//        }
//        onTaskRestoreListener?.onTaskRestore(modelTask)
//    }

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = DoneTasksAdapter(this, this)
        }
    }

    override fun onItemLongClick(location: Int): Boolean {
        Handler().postDelayed({ doneTaskFragmentPresenter.onItemLongClick(location) }, 500)
        return true
    }

    override fun onPriorityClick(modelTask: ModelTask) {
        doneTaskFragmentPresenter.updateTask(modelTask)
    }
}