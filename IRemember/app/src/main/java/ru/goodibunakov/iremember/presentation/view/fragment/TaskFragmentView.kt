package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
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

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showSuccess(text: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showEmptyListText()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideEmptyListText()
}