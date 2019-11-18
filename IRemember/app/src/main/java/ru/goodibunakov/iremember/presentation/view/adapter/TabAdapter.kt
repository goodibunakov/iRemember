package ru.goodibunakov.iremember.presentation.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment

class TabAdapter : FragmentStatePagerAdapter {

    companion object {
        const val CURRENT_TASK_FRAGMENT_POSITION = 0
        const val DONE_TASK_FRAGMENT_POSITION = 1
    }

    private val numberOfTabs: Int
    private val currentTaskFragment: CurrentTaskFragment
    private val doneTaskFragment: DoneTaskFragment

    constructor(fm: FragmentManager, numberOfTabs: Int): super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        this.numberOfTabs = numberOfTabs
        this.currentTaskFragment = CurrentTaskFragment()
        this.doneTaskFragment = DoneTaskFragment()
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            currentTaskFragment
        } else doneTaskFragment
    }

    override fun getCount(): Int {
        return numberOfTabs
    }
}