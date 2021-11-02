package ru.goodibunakov.iremember.presentation

import ru.goodibunakov.iremember.presentation.model.ModelTask

interface OnItemClickListener {
    fun onItemClick(task: ModelTask)
}