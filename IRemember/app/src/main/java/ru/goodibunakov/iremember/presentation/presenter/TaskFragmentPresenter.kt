package ru.goodibunakov.iremember.presentation.presenter

import moxy.MvpPresenter
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragmentView

abstract class TaskFragmentPresenter<V : TaskFragmentView> : MvpPresenter<V>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        getTasksFromDb()
        searchSubscribe()
    }

    protected abstract fun searchSubscribe()

    protected abstract fun getTasksFromDb()
}