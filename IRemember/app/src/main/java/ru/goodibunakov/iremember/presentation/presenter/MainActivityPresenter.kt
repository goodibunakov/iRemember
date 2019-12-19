package ru.goodibunakov.iremember.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.data.SharedPreferencesRepositoryImpl
import ru.goodibunakov.iremember.domain.SharedPreferencesRepository
import ru.goodibunakov.iremember.presentation.bus.Event
import ru.goodibunakov.iremember.presentation.bus.RxBus
import ru.goodibunakov.iremember.presentation.view.activity.MainActivityView

@InjectViewState
class MainActivityPresenter(private val sharedPreferencesRepository: SharedPreferencesRepository, private val bus: RxBus) : MvpPresenter<MainActivityView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (!sharedPreferencesRepository.getBoolean(SharedPreferencesRepositoryImpl.SPLASH_IS_INVISIBLE)) {
            viewState.runSplash()
        }

        viewState.setUI()
    }

    fun onDestroyView() {
        viewState.onDestroyView()
    }

    fun saveBoolean(isChecked: Boolean) {
        sharedPreferencesRepository.putBoolean(SharedPreferencesRepositoryImpl.SPLASH_IS_INVISIBLE, isChecked)
    }

    fun itemSelected(id: Int) {
        viewState.itemSelected(id)
    }

    fun setSplashItemChecked(itemId: Int) {
        val isChecked = sharedPreferencesRepository.getBoolean(SharedPreferencesRepositoryImpl.SPLASH_IS_INVISIBLE)
        viewState.setSplashItemState(itemId, isChecked)
    }

    fun find(event: Event) {
        bus.post(event)
    }
}