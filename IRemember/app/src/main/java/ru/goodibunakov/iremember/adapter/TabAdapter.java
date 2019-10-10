package ru.goodibunakov.iremember.adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import ru.goodibunakov.iremember.fragment.CurrentTaskFragment;
import ru.goodibunakov.iremember.fragment.DoneTaskFragment;

/**
 * Created by GooDi on 18.09.2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    private final int numberOfTabs;
    public final static int CURRENT_TASK_FRAGMENT_POSITION = 0;
    public final static int DONE_TASK_FRAGMENT_POSITION = 1;

    private final CurrentTaskFragment currentTaskFragment;
    private final DoneTaskFragment doneTaskFragment;

    public TabAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.numberOfTabs = numberOfTabs;
        this.currentTaskFragment = new CurrentTaskFragment();
        this.doneTaskFragment = new DoneTaskFragment();
        Log.d("debug", "TabAdapter");
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return currentTaskFragment;
        }
        return doneTaskFragment;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}