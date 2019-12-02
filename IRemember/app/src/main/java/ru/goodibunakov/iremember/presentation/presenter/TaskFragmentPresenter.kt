package ru.goodibunakov.iremember.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragmentView

abstract class TaskFragmentPresenter<V : TaskFragmentView> : MvpPresenter<V>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        getTasksFromDb()
    }

    protected abstract fun getTasksFromDb()

}