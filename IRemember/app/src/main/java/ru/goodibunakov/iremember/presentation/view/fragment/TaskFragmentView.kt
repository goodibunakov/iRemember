package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.goodibunakov.iremember.presentation.model.ModelTask

@StateStrategyType(value = AddToEndStrategy::class)
interface TaskFragmentView : MvpView {
    fun removeAllItemsFromAdapter()
    fun showError(s: Int)
    fun addTask(newTask: ModelTask)
    fun showRemoveTaskDialog(location: Int)
}