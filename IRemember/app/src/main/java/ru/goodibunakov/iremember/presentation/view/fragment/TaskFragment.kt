package ru.goodibunakov.iremember.presentation.view.fragment

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import moxy.MvpAppCompatFragment
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.alarm.AlarmHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.activity.MainActivity
import ru.goodibunakov.iremember.presentation.view.adapter.TasksAdapter

abstract class TaskFragment : MvpAppCompatFragment(), TaskFragmentView {

    var adapter: TasksAdapter? = null
    var activity: MainActivity? = null
    var alarmHelper: AlarmHelper? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (getActivity() != null) {
            activity = getActivity() as MainActivity?
        }

        alarmHelper = AlarmHelper.getInstance()
    }

    override fun addTask(newTask: ModelTask) {
        adapter?.addTask(newTask)
    }

//    abstract fun moveTask(modelTask: ModelTask)

    abstract fun checkAdapter()

    override fun removeAllItemsFromAdapter() {
        adapter?.removeAllItems()
    }

    override fun showRemoveTaskDialog(location: Int) {
        val builder = AlertDialog.Builder(getActivity() as Context)
        builder.setMessage(R.string.dialog_remove_message)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        val item = adapter?.getItem(location)

        if (item!!.isTask()) {
            val removingTask = item as ModelTask
            val timestamp = removingTask.timestamp

            builder.setPositiveButton(R.string.dialog_remove) {
                //TODO
                dialog, _ ->
                adapter?.removeItem(location)
                activity?.dbHelper!!.removeTask(timestamp)
                Toast.makeText(activity, resources.getString(R.string.removed), Toast.LENGTH_SHORT).show()
                alarmHelper?.removeAlarm(timestamp)
                dialog.dismiss()
            }

            builder.setNegativeButton(R.string.dialog_cancel) { dialog, _ -> dialog.cancel() }
        }
        builder.show()
    }
}