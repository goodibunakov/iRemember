package ru.goodibunakov.iremember.presentation.view.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.RememberApp
import ru.goodibunakov.iremember.databinding.ActivityMainBinding
import ru.goodibunakov.iremember.presentation.bus.QueryEvent
import ru.goodibunakov.iremember.presentation.presenter.MainActivityPresenter
import ru.goodibunakov.iremember.presentation.view.adapter.TabAdapter2
import ru.goodibunakov.iremember.presentation.view.dialog.DeleteDoneTasksDialogFragment
import ru.goodibunakov.iremember.presentation.view.fragment.SplashFragment


class MainActivity : MvpAppCompatActivity(R.layout.activity_main), MainActivityView {

    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback
    private val binding by viewBinding(ActivityMainBinding::bind)

    private val mainActivityPresenter by moxyPresenter {
        MainActivityPresenter(
            RememberApp.sharedPreferencesRepository,
            RememberApp.getEventBus(),
            RememberApp.databaseRepository
        )
    }

    companion object {
        const val TOOLBAR_TITLE_PADDING = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("debug", "MainActivity onCreate")
    }

    override fun onResume() {
        super.onResume()
        RememberApp.activityResumed()
    }

    override fun onPause() {
        super.onPause()
        RememberApp.activityPaused()
    }

    override fun runSplash() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, SplashFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (val id = item.itemId) {
            R.id.action_splash -> {
                mainActivityPresenter.itemSelected(id)
                return true
            }

            R.id.action_trash -> {
                mainActivityPresenter.itemSelected(id)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun itemSelected(id: Int) {
        when (id) {
            R.id.action_splash -> {
                val splashItem = binding.toolbar.menu.findItem(id)
                splashItem.isChecked = !splashItem.isChecked
                mainActivityPresenter.saveBoolean(splashItem.isChecked)
            }

            R.id.action_trash -> {
                mainActivityPresenter.showDeleteDoneTasksDialog()
            }
        }
    }

    override fun showDeleteDoneTasksDialog() {
        val dialog = DeleteDoneTasksDialogFragment()
        dialog.show(supportFragmentManager, "DeleteDoneTasksDialogFragment")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d("debug", "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.menu_main, menu)
        val splashItem = menu.findItem(R.id.action_splash)
        mainActivityPresenter.setSplashItemChecked(splashItem.itemId)
        mainActivityPresenter.setDeleteAllDoneTasksIconVisible()
        return true
    }

    override fun setSplashItemState(id: Int, isChecked: Boolean) {
        binding.toolbar.menu?.findItem(id)?.isChecked = isChecked
    }

    override fun setUI() {
        Log.d("debug", "setUI")
        setToolbar()
        setBottomMenuAndViewpager()

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("rx", "onQueryTextChange $newText")
                mainActivityPresenter.find(QueryEvent(newText))
                return true
            }
        })
        binding.searchView.setOnCloseListener {
            Log.d("debug", "close")
            mainActivityPresenter.find(QueryEvent(""))
            false
        }
    }

    override fun onDestroy() {
        mainActivityPresenter.onDestroyView()
        super.onDestroy()
    }

    override fun onDestroyView() {
        binding.searchView.setOnQueryTextListener(null)
        binding.viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    private fun setToolbar() {
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        binding.toolbar.setLogo(R.drawable.toolbar_icon)
        binding.toolbar.titleMarginStart = ContextCompat.getDrawable(
            this,
            R.drawable.toolbar_icon
        )!!.intrinsicWidth + TOOLBAR_TITLE_PADDING
        setSupportActionBar(binding.toolbar)
    }

    private fun setBottomMenuAndViewpager() {
        binding.viewPager2.adapter = TabAdapter2(this)
        binding.bottomMenu.setupWithViewPager2(binding.viewPager2)
        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mainActivityPresenter.showDeleteAllTasksIcon(position == 1)
            }
        }
        binding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
    }


    override fun showDeleteAllTasksIcon(visible: Boolean) {
        binding.toolbar.menu?.findItem(R.id.action_trash)?.isVisible = visible
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {}

    interface OnQueryTextListener : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            return false
        }
    }
}