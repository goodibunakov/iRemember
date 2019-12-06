package ru.goodibunakov.iremember.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.presentation.view.fragment.SplashFragmentVew

@InjectViewState
class SplashFragmentPresenter : MvpPresenter<SplashFragmentVew>() {

    fun haveToClose() {
        viewState.close()
    }
}