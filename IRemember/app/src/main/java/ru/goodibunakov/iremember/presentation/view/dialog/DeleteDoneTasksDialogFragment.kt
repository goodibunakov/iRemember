package ru.goodibunakov.iremember.presentation.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import moxy.MvpAppCompatDialogFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.presentation.presenter.DeleteDoneTasksDialogPresenter

class DeleteDoneTasksDialogFragment : MvpAppCompatDialogFragment(), DeleteDoneTasksDialogView {

    @InjectPresenter
    lateinit var presenter: DeleteDoneTasksDialogPresenter

    @ProvidePresenter
    fun providePresenter(): DeleteDoneTasksDialogPresenter {
        return DeleteDoneTasksDialogPresenter(RememberApp.databaseRepository, RememberApp.getEventBus())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setTitle(R.string.dialog_delete_title)
            setIcon(R.mipmap.ic_launcher)
            setMessage(R.string.dialog_delete_message)
            setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                presenter.okClicked()
            }
            setNegativeButton(R.string.dialog_delete_cancel) { _, _ ->
                presenter.cancelDialog()
            }
        }

        isCancelable = false
        return builder.create()
    }

    override fun cancelDialog() {
        dialog?.cancel()
    }

    override fun dismissRemoveDialog() {
        dialog?.dismiss()
    }

    override fun showError(textId: Int) {
        Toast.makeText(context, getText(textId), Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(textId: Int) {
        Toast.makeText(context, getText(textId), Toast.LENGTH_SHORT).show()
    }
}