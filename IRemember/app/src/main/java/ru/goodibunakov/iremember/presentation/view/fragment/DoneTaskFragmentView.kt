package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.goodibunakov.iremember.presentation.model.ModelTask

@StateStrategyType(value = AddToEndStrategy::class)
interface DoneTaskFragmentView : TaskFragmentView {
    fun checkAdapter()
    override fun addTask(newTask: ModelTask, saveToDb: Boolean)
}