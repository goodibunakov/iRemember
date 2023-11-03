package ru.goodibunakov.iremember.presentation.presenter

import moxy.MvpPresenter
import ru.goodibunakov.iremember.presentation.view.fragment.SplashFragmentVew

class SplashFragmentPresenter : MvpPresenter<SplashFragmentVew>() {

    fun haveToClose() {
        viewState.close()
    }
}