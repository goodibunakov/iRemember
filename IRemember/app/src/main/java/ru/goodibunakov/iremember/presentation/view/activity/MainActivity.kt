package ru.goodibunakov.iremember.presentation.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.alarm.AlarmHelper
import ru.goodibunakov.iremember.data.DbHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.view.adapter.TabAdapter
import ru.goodibunakov.iremember.presentation.view.dialog.AddingTaskDialogFragment
import ru.goodibunakov.iremember.presentation.view.dialog.EditTaskDialogFragment
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.SplashFragment
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragment
import ru.goodibunakov.iremember.utils.PreferenceHelper

class MainActivity : AppCompatActivity(R.layout.activity_main), MainActivityView, AddingTaskDialogFragment.AddingTaskListener,
        DoneTaskFragment.OnTaskRestoreListener, CurrentTaskFragment.OnTaskDoneListener, EditTaskDialogFragment.EditingTaskListener {

    private var preferenceHelper: PreferenceHelper? = null
    private var fragmentManager: FragmentManager? = null
    private var currentTaskFragment: TaskFragment? = null
    private var doneTaskFragment: TaskFragment? = null

    var dbHelper: DbHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceHelper = PreferenceHelper.getInstance()
        preferenceHelper!!.init(applicationContext)

        dbHelper = DbHelper(applicationContext)
        AlarmHelper.getInstance().initAlarmManager()

        fragmentManager = supportFragmentManager
        runSplash()
        setUI()
    }

    override fun onResume() {
        super.onResume()
        RememberApp.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        RememberApp.activityPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        searchView.setOnQueryTextListener(null)
    }

    private fun runSplash() {
        if (!preferenceHelper!!.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE)) {
            val splashFragment = SplashFragment()
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.content_frame, splashFragment)
                    ?.addToBackStack(null)
                    ?.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_splash) {
            item.isChecked = !item.isChecked
            preferenceHelper?.putBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE, item.isChecked)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val splashItem = menu.findItem(R.id.action_splash)
        splashItem.isChecked = preferenceHelper!!.getBoolean(PreferenceHelper.SPLASH_IS_INVISIBLE)
        return true
    }

    private fun setUI() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        toolbar.setLogo(R.drawable.toolbar_icon)
        toolbar.titleMarginStart = ContextCompat.getDrawable(this, R.drawable.toolbar_icon)!!.intrinsicWidth + 2
        setSupportActionBar(toolbar)

        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_task))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_task))

        val tabAdapter = TabAdapter(fragmentManager!!, 2)

        viewPager.adapter = tabAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        currentTaskFragment = tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION) as CurrentTaskFragment
        doneTaskFragment = tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION) as DoneTaskFragment

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("debug", "onQueryTextChange = $newText")
                currentTaskFragment?.findTasks(newText)
                doneTaskFragment?.findTasks(newText)
                return false
            }
        })
    }

    override fun onTaskAdded(newTask: ModelTask) {
        currentTaskFragment!!.addTask(newTask, true)
    }

    override fun onTaskDone(modelTask: ModelTask) {
        doneTaskFragment!!.addTask(modelTask, false)
    }

    override fun onTaskRestore(modelTask: ModelTask) {
        currentTaskFragment!!.addTask(modelTask, false)
    }

    override fun onTaskEdited(updatedTask: ModelTask) {
        currentTaskFragment!!.updateTask(updatedTask)
        dbHelper?.update()?.task(updatedTask)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {

    }
}