package ru.goodibunakov.iremember.presentation.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
import ru.goodibunakov.iremember.presentation.view.adapter.TabAdapter2
import ru.goodibunakov.iremember.presentation.view.fragment.CurrentTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.DoneTaskFragment
import ru.goodibunakov.iremember.presentation.view.fragment.SplashFragment

class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainActivityView,
        DoneTaskFragment.OnTaskRestoreListener,
        CurrentTaskFragment.OnTaskDoneListener {

    private var fragmentManager: FragmentManager? = null

    var dbHelper: DbHelper? = null

    @InjectPresenter
    lateinit var mainActivityPresenter: MainActivityPresenter

    @ProvidePresenter
    fun providePresenter(): MainActivityPresenter {
        return MainActivityPresenter(RememberApp.databaseRepository, RememberApp.sharedPreferencesRepository)
    }

    companion object {
        const val TOOLBAR_TITLE_PADDING = 2
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
        viewPager2.adapter = TabAdapter2(this)
        TabLayoutMediator(tabLayout, viewPager2,
                TabLayoutMediator.TabConfigurationStrategy { tab, position ->
                    if (position == 0) tab.text = getString(R.string.current_task)
                    else tab.text = getString(R.string.done_task)
                }).attach()
    }

    override fun onTaskDone(modelTask: ModelTask) {
//        doneTaskFragment!!.addTask(modelTask)
    }

    override fun onTaskRestore(modelTask: ModelTask) {
//        currentTaskFragment!!.addTask(modelTask)
    }

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