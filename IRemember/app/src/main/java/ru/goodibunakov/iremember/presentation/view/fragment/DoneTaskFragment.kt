package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.databinding.FragmentDoneTaskBinding
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.DoneTaskFragmentPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.DoneTasksAdapter
import ru.goodibunakov.iremember.presentation.view.dialog.RemoveTaskDialog

class DoneTaskFragment : TaskFragment(R.layout.fragment_done_task),
    DoneTaskFragmentView,
    OnItemLongClickListener,
    OnPriorityClickListener {

    @InjectPresenter
    lateinit var doneTaskFragmentPresenter: DoneTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): DoneTaskFragmentPresenter {
        return DoneTaskFragmentPresenter(RememberApp.getEventBus())
    }

    private val binding by viewBinding(FragmentDoneTaskBinding::bind)

    init {
        adapter = DoneTasksAdapter(this, this)
    }

    override fun showError(s: Int) {
        Toast.makeText(context, getText(s), Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = DoneTasksAdapter(this, this)
        }
        removeAllItemsFromAdapter()
    }

    override fun onItemLongClick(location: Int): Boolean {
        Handler(Looper.getMainLooper()).postDelayed({
            doneTaskFragmentPresenter.onItemLongClick(
                location
            )
        }, 500)
        return true
    }

    override fun onPriorityClick(modelTask: ModelTask) {
        doneTaskFragmentPresenter.updateTask(modelTask)
        doneTaskFragmentPresenter.setAlarm(modelTask)
    }

    override fun showRemoveTaskDialog(location: Int) {
        dialog = RemoveTaskDialog.generateRemoveDialog(requireContext()) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                doneTaskFragmentPresenter.doRemove(
                    location,
                    (adapter?.getItem(location) as ModelTask).timestamp
                )
            } else {
                doneTaskFragmentPresenter.cancelDialog()
            }
        }.show()
    }

    override fun cancelRemoveDialog() {
        dialog.cancel()
    }

    override fun showEmptyListText() {
        binding.emptyListText.visibility = View.VISIBLE
    }

    override fun hideEmptyListText() {
        binding.emptyListText.visibility = View.GONE
    }
}