package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.goodibunakov.iremember.presentation.model.ModelTask

@StateStrategyType(value = AddToEndStrategy::class)
interface CurrentTaskFragmentView : TaskFragmentView {
    fun showAddingTaskDialog()
    override fun checkAdapter()
    fun showFab()
    fun hideFab()
    fun showEditTaskDialog(task: ModelTask)
    override fun cancelRemoveDialog()
}