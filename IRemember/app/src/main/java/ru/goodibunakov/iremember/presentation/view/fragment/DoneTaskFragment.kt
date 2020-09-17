package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import ru.goodibunakov.iremember.presentation.view.dialog.RemoveTaskDialog

class DoneTaskFragment : TaskFragment(), DoneTaskFragmentView, OnItemLongClickListener, OnPriorityClickListener {

    @InjectPresenter
    lateinit var doneTaskFragmentPresenter: DoneTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): DoneTaskFragmentPresenter {
        return DoneTaskFragmentPresenter(RememberApp.getEventBus())
    }

    init {
        adapter = DoneTasksAdapter(this, this)
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

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = DoneTasksAdapter(this, this)
        }
        removeAllItemsFromAdapter()
    }

    override fun onItemLongClick(location: Int): Boolean {
        Handler(Looper.getMainLooper()).postDelayed({ doneTaskFragmentPresenter.onItemLongClick(location) }, 500)
        return true
    }

    override fun onPriorityClick(modelTask: ModelTask) {
        doneTaskFragmentPresenter.updateTask(modelTask)
        doneTaskFragmentPresenter.setAlarm(modelTask)
    }

    override fun showRemoveTaskDialog(location: Int) {
        dialog = RemoveTaskDialog.generateRemoveDialog(context!!) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                doneTaskFragmentPresenter.doRemove(location, (adapter?.getItem(location) as ModelTask).timestamp)
            } else {
                doneTaskFragmentPresenter.cancelDialog()
            }
        }.show()
    }

    override fun cancelRemoveDialog() {
        dialog.cancel()
    }

    override fun showEmptyListText() {
        emptyListText.visibility = View.VISIBLE
    }

    override fun hideEmptyListText() {
        emptyListText.visibility = View.GONE
    }
}