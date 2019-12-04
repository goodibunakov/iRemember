package ru.goodibunakov.iremember.presentation

import ru.goodibunakov.iremember.presentation.model.ModelTask

interface OnPriorityClickListener {
    fun onPriorityClick(modelTask: ModelTask)
}