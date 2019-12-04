package ru.goodibunakov.iremember.presentation.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.alarm.AlarmHelper
import ru.goodibunakov.iremember.data.DbHelper
import ru.goodibunakov.iremember.presentation.model.ModelTask
import ru.goodibunakov.iremember.presentation.presenter.MainActivityPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.TabAdapter
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.SplashFragment
import ru.goodibunakov.iremember.presentation.view.fragment.TaskFragment

class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainActivityView,
        DoneTaskFragment.OnTaskRestoreListener,
        CurrentTaskFragment.OnTaskDoneListener {

    private var fragmentManager: FragmentManager? = null
    private var currentTaskFragment: TaskFragment? = null
    private var doneTaskFragment: TaskFragment? = null

    var dbHelper: DbHelper? = null

    @InjectPresenter
    lateinit var mainActivityPresenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter(): MainActivityPresenter {
        return MainActivityPresenter(RememberApp.databaseRepository, RememberApp.sharedPreferencesRepository)
    }

    companion object {
        const val TOOLBAR_TITLE_PADDING = 2
        const val NUMBER_OF_TABS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DbHelper(applicationContext)
        AlarmHelper.getInstance().initAlarmManager()

        fragmentManager = supportFragmentManager
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
        mainActivityPresenter.onDestroyView()
        super.onDestroy()
    }

    override fun onDestroyView() {
        searchView.setOnQueryTextListener(null)
    }

    override fun runSplash() {
        val splashFragment = SplashFragment()
        fragmentManager?.beginTransaction()
                ?.replace(R.id.content_frame, splashFragment)
                ?.addToBackStack(null)
                ?.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_splash) {
            mainActivityPresenter.itemSelected(id)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun itemSelected(id: Int) {
        if (id == R.id.action_splash) {
            val splashItem = toolbar.menu.findItem(id)
            splashItem!!.isChecked = !splashItem.isChecked
            mainActivityPresenter.saveBoolean(splashItem.isChecked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val splashItem = menu.findItem(R.id.action_splash)
        mainActivityPresenter.setSplashItemChecked(splashItem.itemId)
        return true
    }

    override fun setSplashItemState(id: Int, isChecked: Boolean) {
        toolbar.menu.findItem(id).isChecked = isChecked
    }

    override fun setUI() {
        setToolbar()
        setTabLayout()

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                mainActivityPresenter.find(newText)
//                currentTaskFragment?.findTasks(newText)
//                doneTaskFragment?.findTasks(newText)
                return false
            }
        })
    }

    private fun setToolbar() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        toolbar.setLogo(R.drawable.toolbar_icon)
        toolbar.titleMarginStart = ContextCompat.getDrawable(this, R.drawable.toolbar_icon)!!.intrinsicWidth + TOOLBAR_TITLE_PADDING
        setSupportActionBar(toolbar)
    }

    private fun setTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.current_task))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.done_task))

        val tabAdapter = TabAdapter(fragmentManager!!, NUMBER_OF_TABS)

        viewPager.adapter = tabAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
        })

        currentTaskFragment = tabAdapter.getItem(TabAdapter.CURRENT_TASK_FRAGMENT_POSITION) as CurrentTaskFragment
        doneTaskFragment = tabAdapter.getItem(TabAdapter.DONE_TASK_FRAGMENT_POSITION) as DoneTaskFragment
    }

//    override fun onTaskAdded(newTask: ModelTask) {
//        currentTaskFragment!!.addTask(newTask, true)
//    }

    override fun onTaskDone(modelTask: ModelTask) {
        doneTaskFragment!!.addTask(modelTask)
    }

    override fun onTaskRestore(modelTask: ModelTask) {
        currentTaskFragment!!.addTask(modelTask)
    }

//    override fun onTaskEdited(updatedTask: ModelTask) {
//        currentTaskFragment!!.updateTask(updatedTask)
//        dbHelper?.update()?.task(updatedTask)
//    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {

    }

    interface OnTabSelectedListener : TabLayout.OnTabSelectedListener {
        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }

    interface OnQueryTextListener : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }
    }
}