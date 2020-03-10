package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.viewstate.strategy.*
import ru.goodibunakov.iremember.presentation.model.ModelTask

@StateStrategyType(value = AddToEndStrategy::class)
interface CurrentTaskFragmentView : TaskFragmentView {
    @StateStrategyType(SkipStrategy::class)
    fun showAddingTaskDialog()
    override fun checkAdapter()
    fun showFab()
    fun hideFab()

    @StateStrategyType(SkipStrategy::class)
    fun showEditTaskDialog(task: ModelTask)
    override fun cancelRemoveDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun initRateBottomSheet()
}