package ru.goodibunakov.iremember.presentation.view.fragment

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface SplashFragmentVew: MvpView {
    fun close()
}