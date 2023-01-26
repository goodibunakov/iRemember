package ru.goodibunakov.iremember.presentation.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import moxy.MvpAppCompatFragment
import ru.goodibunakov.iremember.presentation.alarm.AlarmHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.adapter.TasksAdapter

abstract class TaskFragment(@LayoutRes layout: Int) : MvpAppCompatFragment(layout), TaskFragmentView {

    var adapter: TasksAdapter? = null
    private var alarmHelper: AlarmHelper? = null

    protected lateinit var dialog: Dialog

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        alarmHelper = AlarmHelper.getInstance()
    }

    override fun addTask(newTask: ModelTask) {
        adapter?.addTask(newTask)
    }

    abstract override fun checkAdapter()

    override fun removeAllItemsFromAdapter() {
        adapter?.removeAllItems()
    }

    override fun removeItemFromAdapter(location: Int) {
        adapter?.removeItem(location)
    }

    override fun dismissRemoveDialog() {
        dialog.dismiss()
    }

    override fun showSuccess(text: Int) {
        Toast.makeText(context, getString(text), Toast.LENGTH_SHORT).show()
    }
}