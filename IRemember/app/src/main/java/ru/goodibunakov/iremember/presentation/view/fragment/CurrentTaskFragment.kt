package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.databinding.FragmentCurrentTaskBinding
import ru.goodibunakov.iremember.presentation.OnItemClickListener
import ru.goodibunakov.iremember.presentation.OnItemLongClickListener
import ru.goodibunakov.iremember.presentation.OnPriorityClickListener
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.CurrentTaskFragmentPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.CurrentTasksAdapter
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment
import ru.goodibunakov.iremember.presentation.view.dialog.EditTaskDialogFragment
import ru.goodibunakov.iremember.presentation.view.dialog.RemoveTaskDialog

class CurrentTaskFragment : TaskFragment(R.layout.fragment_current_task),
    CurrentTaskFragmentView,
    OnItemClickListener,
    OnItemLongClickListener, OnPriorityClickListener {


    @InjectPresenter
    lateinit var currentTaskFragmentPresenter: CurrentTaskFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): CurrentTaskFragmentPresenter {
        return CurrentTaskFragmentPresenter(RememberApp.getEventBus())
    }

    private val binding by viewBinding(FragmentCurrentTaskBinding::bind)

    init {
        adapter = CurrentTasksAdapter(this, this, this)
    }

    override fun showError(s: Int) {
        Toast.makeText(context, getText(s), Toast.LENGTH_SHORT).show()
    }

    override fun showRemoveTaskDialog(location: Int) {
        dialog = RemoveTaskDialog.generateRemoveDialog(requireContext()) { _, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                currentTaskFragmentPresenter.doRemove(
                    location,
                    (adapter?.getItem(location) as ModelTask).timestamp
                )
            } else {
                currentTaskFragmentPresenter.cancelDialog()
            }
        }.show()
    }

    override fun cancelRemoveDialog() {
        dialog.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        binding.fab.setOnClickListener {
            currentTaskFragmentPresenter.showViewToAddTask()
        }
    }

    override fun showAddingTaskDialog() {
        val addingTaskDialogFragment = AddingTaskDialogFragment()
        addingTaskDialogFragment.show(
            requireActivity().supportFragmentManager,
            "AddingTaskDialogFragment"
        )
        Log.d("debug", "addingTaskDialogFragment.show")
    }

    private fun initRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.fab.isShown) {
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
        binding.fab.hide()
    }

    override fun showFab() {
        binding.fab.show()
    }

    override fun checkAdapter() {
        if (adapter == null) {
            adapter = CurrentTasksAdapter(this, this, this)
        }
        removeAllItemsFromAdapter()
    }

    override fun onItemClick(task: ModelTask) {
        currentTaskFragmentPresenter.onItemClick(task)
    }

    override fun showEditTaskDialog(task: ModelTask) {
        val editingDialog = EditTaskDialogFragment.newInstance(task)
        editingDialog.show(requireActivity().supportFragmentManager, "EditTaskDialogFragment")
    }

    override fun onItemLongClick(location: Int): Boolean {
        Handler(Looper.getMainLooper()).postDelayed({
            currentTaskFragmentPresenter.onItemLongClick(
                location
            )
        }, 500)
        return true
    }

    override fun onPriorityClick(modelTask: ModelTask) {
        currentTaskFragmentPresenter.updateTask(modelTask)
        currentTaskFragmentPresenter.removeAlarm(modelTask.timestamp)
    }

    override fun showEmptyListText() {
        binding.emptyListText.visibility = View.VISIBLE
    }

    override fun hideEmptyListText() {
        binding.emptyListText.visibility = View.GONE
    }
}