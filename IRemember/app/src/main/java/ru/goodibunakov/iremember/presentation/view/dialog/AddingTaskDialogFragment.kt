package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.dialog_task.view.*
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.presenter.AddingTaskDialogPresenter

class AddingTaskDialogFragment : MvpAppCompatDialogFragment(), AddingTaskDialogFragmentView {

    private lateinit var container: View
    private lateinit var positive: Button

    @InjectPresenter
    lateinit var addingTaskDialogPresenter: AddingTaskDialogPresenter

    @ProvidePresenter
    fun providePresenter(): AddingTaskDialogPresenter {
        return AddingTaskDialogPresenter(RememberApp.databaseRepository)
    }

    private val datePickerController = object : DatePickerController() {
        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            addingTaskDialogPresenter.dateSelected(year, month, dayOfMonth)
        }
    }

    private val timePickerController = object : TimePickerController() {
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
            addingTaskDialogPresenter.timeSelected(hourOfDay, minute)
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity as Context)
        container = View.inflate(activity, R.layout.dialog_task, null)

        builder.setTitle(R.string.dialog_title)
        builder.setIcon(R.mipmap.ic_launcher)

        builder.setView(container)

        addingTaskDialogPresenter.dialogCreated()

        builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
            addingTaskDialogPresenter.okClicked(container.etTitle.text.toString())
            if (container.etDate.length() != 0 || container.etTime.length() != 0) {
                addingTaskDialogPresenter.setDateToModel()
//                val alarmHelper = AlarmHelper.getInstance()
//                alarmHelper.setAlarm(modelTask)
            }
            addingTaskDialogPresenter.saveTask()
            addingTaskDialogPresenter.dismissDialog()
        }
        builder.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            addingTaskDialogPresenter.cancelDialog()
        }


        val priorityAdapter = ArrayAdapter<String>(activity!!,
                android.R.layout.simple_spinner_dropdown_item,
                activity!!.resources.getStringArray(R.array.priority_array))
        container.spinnerPriority.adapter = priorityAdapter
        container.spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                addingTaskDialogPresenter.itemSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                addingTaskDialogPresenter.nothingSelected()
            }
        }


        container.etDate.setOnClickListener {
            if (container.etDate.length() == 0) {
                addingTaskDialogPresenter.dateIsEmpty()
            }
            addingTaskDialogPresenter.editTextDateClicked()
        }

        container.etTime.setOnClickListener {
            if (container.etTime.length() == 0) {
                addingTaskDialogPresenter.timeIsEmpty()
            }
            addingTaskDialogPresenter.editTextTimeClicked()
        }

        val alertDialog = builder.create()

        alertDialog.setOnShowListener { dialog ->
            positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)
            if (container.etTitle.length() == 0) {
                addingTaskDialogPresenter.titleEmpty()
            }

            container.etTitle.addTextChangedListener(object : MyTextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isEmpty()) {
                        addingTaskDialogPresenter.titleEmpty()
                    } else {
                        addingTaskDialogPresenter.titleNotEmpty()
                    }
                }
            })
        }
        return alertDialog
    }

    override fun dismissDialog() {
        dialog?.dismiss()
    }

    override fun cancelDialog() {
        dialog?.cancel()
    }

    override fun setEmptyToDateEditText() {
        container.etDate.setText("")
    }

    override fun setEmptyToTimeEditText() {
        container.etTime.setText("")
    }

    override fun showDatePickerController() {
        datePickerController.onCreateDialog(activity as Context).show()
    }

    override fun showTimePickerController() {
        timePickerController.onCreateDialog(activity as Context).show()
    }

    override fun setDate(date: String) {
        container.etDate.setText(date)
    }

    override fun setTime(time: String) {
        container.etTime.setText(time)
    }

    override fun setUIWhenTitleEmpty() {
        positive.isEnabled = false
        container.dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
    }

    override fun setUIWhenTitleNotEmpty() {
        positive.isEnabled = true
        container.dialogTaskTitle.isErrorEnabled = false
    }
}