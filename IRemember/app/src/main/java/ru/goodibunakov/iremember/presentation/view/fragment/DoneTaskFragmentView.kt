package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface DoneTaskFragmentView : TaskFragmentView {
    override fun checkAdapter()
    override fun cancelRemoveDialog()
}