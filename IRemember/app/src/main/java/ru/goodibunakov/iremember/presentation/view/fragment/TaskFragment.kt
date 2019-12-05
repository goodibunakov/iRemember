package ru.goodibunakov.iremember.presentation.view.fragment

import android.app.Dialog
import android.os.Bundle
import moxy.MvpAppCompatFragment
import ru.goodibunakov.iremember.alarm.AlarmHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.adapter.TasksAdapter


abstract class TaskFragment : MvpAppCompatFragment(), TaskFragmentView {

    var adapter: TasksAdapter? = null
    var alarmHelper: AlarmHelper? = null

    protected lateinit var dialog: Dialog

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

    override fun removeAlarm() {

    }

    override fun removeItemFromAdapter(location: Int) {
        adapter?.removeItem(location)
    }

    override fun dismissRemoveDialog() {
        dialog.dismiss()
    }
}