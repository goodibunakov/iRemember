package ru.goodibunakov.iremember.adapter;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.goodibunakov.iremember.fragment.CurrentTaskFragment;
import ru.goodibunakov.iremember.fragment.DoneTaskFragment;

/**
 * Created by GooDi on 18.09.2017.
 */

public class TabAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;
    public final static int CURRENT_TASK_FRAGMENT_POSITION = 0;
    public final static int DONE_TASK_FRAGMENT_POSITION = 1;

    private CurrentTaskFragment currentTaskFragment;
    private DoneTaskFragment doneTaskFragment;

    public TabAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
        this.currentTaskFragment = new CurrentTaskFragment();
        this.doneTaskFragment = new DoneTaskFragment();
        Log.d("debug", "TabAdapter");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return currentTaskFragment;
            case 1:
                return doneTaskFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
