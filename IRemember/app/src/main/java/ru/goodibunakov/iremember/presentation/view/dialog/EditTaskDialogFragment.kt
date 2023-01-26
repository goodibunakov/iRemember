package ru.goodibunakov.iremember.presentation.view.dialog


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.databinding.DialogTaskBinding
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.EditTaskDialogPresenter
import ru.goodibunakov.iremember.presentation.utils.DateUtils
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_DATE_ONLY
import ru.goodibunakov.iremember.presentation.utils.DateUtils.FORMAT_TIME_ONLY
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_NEGATIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.KEY_POSITIVE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_DATE
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment.Companion.REQUEST_CODE_TIME

class EditTaskDialogFragment : MvpAppCompatDialogFragment(), EditTaskDialogFragmentView {

    private var _binding: DialogTaskBinding? = null
    // This property is only valid between onCreateDialog and onDestroyView.
    private val binding get() = _binding!!

//    private lateinit var container: View
    private lateinit var positive: Button
    private lateinit var timePickerDialogFragment: TimePickerDialogFragment
    private lateinit var datePickerDialogFragment: DatePickerDialogFragment

    @InjectPresenter
    lateinit var editTaskDialogPresenter: EditTaskDialogPresenter

    @ProvidePresenter
    fun providePresenter(): EditTaskDialogPresenter {
        return EditTaskDialogPresenter(RememberApp.databaseRepository)
    }

    companion object {
        fun newInstance(task: ModelTask): EditTaskDialogFragment {
            val editTaskDialogFragment = EditTaskDialogFragment()

            val args = Bundle()
            args.putString("title", task.title)
            args.putLong("date", task.date)
            args.putInt("priority", task.priority)
            args.putLong("timestamp", task.timestamp)

            editTaskDialogFragment.arguments = args
            return editTaskDialogFragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogTaskBinding.inflate(LayoutInflater.from(context))

        val args = arguments
        if (args != null) {
            val title = args.getString("title")!!
            val date = args.getLong("date")
            val priority = args.getInt("priority")
            val timestamp = args.getLong("timestamp")
            editTaskDialogPresenter.onCreateDialog(title, date, priority, timestamp)

            val builder = AlertDialog.Builder(activity as Context, R.style.AppThemeDialog)
//            container = View.inflate(context, R.layout.dialog_task, null)
            isCancelable = false

            builder.setTitle(R.string.editing_title)
            builder.setIcon(R.mipmap.ic_launcher)
            editTaskDialogPresenter.initTitle()

            if (date != 0L) {
                editTaskDialogPresenter.setDateToUI()
                editTaskDialogPresenter.setTimeToUI()
            }

            binding.dialogTaskTitle.hint = resources.getString(R.string.task_title)
            binding.dialogTaskDate.hint = resources.getString(R.string.task_date)
            binding.dialogTaskTime.hint = resources.getString(R.string.task_time)

            builder.setView(binding.root)

            builder.setPositiveButton(R.string.dialog_ok) { _, _ ->
                editTaskDialogPresenter.okClicked(binding.etTitle.text.toString().trim())

                if (binding.etDate.length() != 0 || binding.etTime.length() != 0) {
                    editTaskDialogPresenter.setDateToModel()
                }
                editTaskDialogPresenter.updateTask()
                editTaskDialogPresenter.dismissDialog()
            }
            builder.setNegativeButton(R.string.dialog_cancel) { _, _ -> editTaskDialogPresenter.cancelDialog() }


            val priorityAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                requireActivity().resources.getStringArray(R.array.priority_array)
            )

            binding.spinnerPriority.adapter = priorityAdapter
            editTaskDialogPresenter.setPriorityToUI()

            binding.spinnerPriority.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        editTaskDialogPresenter.itemSelected(position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        editTaskDialogPresenter.nothingSelected()
                    }
                }

            val alertDialog = builder.create()

            alertDialog.setOnShowListener { dialog ->
                positive = (dialog as AlertDialog).getButton(DialogInterface.BUTTON_POSITIVE)

                if (binding.etTitle.length() == 0) {
                    editTaskDialogPresenter.titleEmpty()
                }

                binding.etTitle.addTextChangedListener(object : MyTextWatcher {
                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        editTaskDialogPresenter.onTextChanged(s)
                    }
                })

                binding.etTitle.isFocusableInTouchMode = true
                editTaskDialogPresenter.getTitleHasFocus()
                binding.etTitle.onFocusChangeListener =
                    View.OnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            editTaskDialogPresenter.setTitleHasFocus(hasFocus)
                        }
                    }

                binding.etDate.setOnClickListener {
                    clearTitleFocus()
                    if (binding.etDate.length() == 0) {
                        editTaskDialogPresenter.setEmptyDateToEditText()
                    }
                    editTaskDialogPresenter.dateClicked()
                }

                binding.etTime.setOnClickListener {
                    if (binding.etTime.length() == 0) {
                        editTaskDialogPresenter.setEmptyTimeToEditText()
                    }
                    editTaskDialogPresenter.timeClicked()
                }
            }

            return alertDialog
        } else {
            return super.onCreateDialog(savedInstanceState)
        }
    }

    override fun setDateToUI(date: Long) {
        binding.etDate.setText(DateUtils.getDate(date, FORMAT_DATE_ONLY))
    }

    override fun setTimeToUI(time: Long) {
        binding.etTime.setText(DateUtils.getDate(time, FORMAT_TIME_ONLY))
    }

    override fun setEmptyDateToEditText() {
        binding.etDate.setText("")
    }

    override fun showDatePickerDialog() {
        datePickerDialogFragment = DatePickerDialogFragment()
        datePickerDialogFragment.setTargetFragment(this@EditTaskDialogFragment, REQUEST_CODE_DATE)
        datePickerDialogFragment.show(
            requireActivity().supportFragmentManager,
            "DatePickerDialogFragment"
        )
    }

    override fun showTimePickerDialog() {
        timePickerDialogFragment = TimePickerDialogFragment()
        timePickerDialogFragment.setTargetFragment(this@EditTaskDialogFragment, REQUEST_CODE_TIME)
        timePickerDialogFragment.show(
            requireActivity().supportFragmentManager,
            "TimePickerDialogFragment"
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_TIME) {
                when (data?.extras?.getString(AddingTaskDialogFragment.TYPE)) {
                    KEY_POSITIVE -> {
                        getTimeFromIntent(data)
                        editTaskDialogPresenter.positiveTimeClicked()
                    }
                    KEY_NEGATIVE -> editTaskDialogPresenter.negativeTimeClicked()
                }
            } else if (requestCode == REQUEST_CODE_DATE) {
                when (data?.extras?.getString(AddingTaskDialogFragment.TYPE)) {
                    KEY_POSITIVE -> {
                        getDateFromIntent(data)
                        editTaskDialogPresenter.positiveDateClicked()
                    }
                    KEY_NEGATIVE -> editTaskDialogPresenter.negativeDateClicked()
                }
            }
        }
    }

    private fun getTimeFromIntent(data: Intent?) {
        val hourOfDay = data?.extras?.getInt(AddingTaskDialogFragment.HOUR)
        val minute = data?.extras?.getInt(AddingTaskDialogFragment.MINUTE)
        if (hourOfDay != null && minute != null) {
            editTaskDialogPresenter.timeSelected(hourOfDay, minute)
        }
    }

    private fun getDateFromIntent(data: Intent?) {
        val year = data?.extras?.getInt(AddingTaskDialogFragment.YEAR)
        val month = data?.extras?.getInt(AddingTaskDialogFragment.MONTH)
        val day = data?.extras?.getInt(AddingTaskDialogFragment.DAY)
        if (year != null && month != null && day != null) {
            editTaskDialogPresenter.dateSelected(year, month, day)
        }
    }

    override fun setTimeToEditText(time: String) {
        binding.etTime.setText(time)
    }

    override fun setDateToEditText(date: String) {
        binding.etDate.setText(date)
    }

    override fun setEmptyTimeToEditText() {
        binding.etTime.setText("")
    }

    override fun dismissDialog() {
        dialog?.dismiss()
    }

    override fun cancelDialog() {
        dialog?.cancel()
    }

    override fun setUIWhenTitleEmpty() {
        positive.isEnabled = false
        binding.dialogTaskTitle.error = resources.getString(R.string.dialog_error_empty_title)
    }

    override fun setUIWhenTitleNotEmpty() {
        positive.isEnabled = true
        binding.dialogTaskTitle.isErrorEnabled = false
    }

    override fun initTitle(title: String) {
        binding.etTitle.setText(title)
        binding.etTitle.setSelection(binding.etTitle.length())
    }

    override fun setTitleFocus(hasFocus: Boolean) {
        if (hasFocus) {
            binding.etTitle.requestFocus()
            dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

    override fun setPriorityToUI(priority: Int) {
        binding.spinnerPriority.setSelection(priority)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideSoftKeyboard()
        _binding = null
    }

    override fun closeTimeDialogFragment() {
        if (::timePickerDialogFragment.isInitialized) {
            timePickerDialogFragment.dismiss()
        }
    }

    override fun closeDateDialogFragment() {
        if (::datePickerDialogFragment.isInitialized) {
            datePickerDialogFragment.dismiss()
        }
    }

    private fun clearTitleFocus() {
        if (binding.etTitle.hasFocus()) {
            binding.etTitle.clearFocus()
            hideSoftKeyboard()
            editTaskDialogPresenter.setTitleHasFocus(false)
        }
    }

    private fun hideSoftKeyboard() {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding.etTitle.windowToken,
            0
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }
}