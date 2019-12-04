package ru.goodibunakov.iremember.presentation.presenter

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.data.SharedPreferencesRepositoryImpl
import ru.goodibunakov.iremember.domain.DatabaseRepository
import ru.goodibunakov.iremember.domain.SharedPreferencesRepository
import ru.goodibunakov.iremember.presentation.view.activity.MainActivityView

@InjectViewState
class MainActivityPresenter(private val databaseRepository: DatabaseRepository,
                            private val sharedPreferencesRepository: SharedPreferencesRepository)
    : MvpPresenter<MainActivityView>() {

    private var disp: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (!sharedPreferencesRepository.getBoolean(SharedPreferencesRepositoryImpl.SPLASH_IS_INVISIBLE)) {
            viewState.runSplash()
        }
        viewState.setUI()
    }

    fun onDestroyView() {
        viewState.onDestroyView()
        if (disp != null && !disp!!.isDisposed)
            disp!!.dispose()
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

    override fun onDestroy() {
        disp?.dispose()
        super.onDestroy()
    }

    fun find(newText: String) {
        RememberApp.getBus().post(newText)
    }
}