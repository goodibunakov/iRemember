package ru.goodibunakov.iremember.presentation.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment

class TabAdapter2(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val TAB_COUNT = 2
    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) CurrentTaskFragment() else DoneTaskFragment()
    }
}