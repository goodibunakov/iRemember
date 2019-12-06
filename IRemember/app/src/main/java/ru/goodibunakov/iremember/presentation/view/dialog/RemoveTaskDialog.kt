package ru.goodibunakov.iremember.presentation.view.dialog

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import ru.goodibunakov.iremember.R

class RemoveTaskDialog {

    companion object {
        fun generateRemoveDialog(context: Context, listener: DialogInterface.OnClickListener): AlertDialog.Builder {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.dialog_remove_message)
            builder.setTitle(R.string.app_name)
            builder.setIcon(R.mipmap.ic_launcher)

            builder.setPositiveButton(R.string.dialog_remove, listener)
            builder.setNegativeButton(R.string.dialog_cancel, listener)
            return builder
        }
    }
}