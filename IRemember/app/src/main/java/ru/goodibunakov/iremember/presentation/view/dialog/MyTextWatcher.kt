package ru.goodibunakov.iremember.presentation.view.dialog

import android.text.Editable
import android.text.TextWatcher

interface MyTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}
}