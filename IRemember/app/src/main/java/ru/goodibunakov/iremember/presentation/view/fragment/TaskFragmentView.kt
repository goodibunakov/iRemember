package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.goodibunakov.iremember.presentation.model.ModelTask

@StateStrategyType(value = AddToEndStrategy::class)
interface TaskFragmentView : MvpView {
    fun checkAdapter()
    fun removeAllItemsFromAdapter()
    fun removeItemFromAdapter(location: Int)
    fun showError(s: Int)
    fun addTask(newTask: ModelTask)
    fun showRemoveTaskDialog(location: Int)
    fun cancelRemoveDialog()
    fun dismissRemoveDialog()
    fun showSuccess(text: Int)
}