package ru.goodibunakov.iremember.presentation.view.activity

import android.view.MenuItem
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = AddToEndStrategy::class)
interface MainActivityView : MvpView {
    fun onDestroyView()
    fun runSplash()
    fun setUI()
    fun itemSelected(id: Int)
    fun setSplashItemState(id: Int, isChecked: Boolean)
}
