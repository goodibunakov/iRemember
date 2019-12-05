package ru.goodibunakov.iremember.presentation.view.fragment

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.fragment_splash.view.*
import ru.goodibunakov.iremember.BuildConfig
import ru.goodibunakov.iremember.R

class SplashFragment : Fragment(), Animation.AnimationListener {

    private var splashIn: Animation? = null
    private var splashOut: Animation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        view.version.text = String.format(resources.getString(R.string.version), BuildConfig.VERSION_NAME)
        SplashTask().execute()

        splashIn = AnimationUtils.loadAnimation(activity, R.anim.splash_anim_in)
        splashIn?.setAnimationListener(this)
        container?.startAnimation(splashIn)
        splashOut = AnimationUtils.loadAnimation(activity, R.anim.splash_anim_out)
        splashOut?.setAnimationListener(this)
        splashOut?.startOffset = 1000

        return view
    }

    override fun onAnimationStart(animation: Animation) {}

    override fun onAnimationEnd(animation: Animation) {
        if (animation === splashIn) {
            view?.startAnimation(splashOut)
        }
        if (animation === splashOut) {
            if (activity != null) {
                activity!!.supportFragmentManager
                        .beginTransaction()
                        .remove(this@SplashFragment)
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onAnimationRepeat(animation: Animation) {}

    private class SplashTask internal constructor() : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {

            try {
                Thread.sleep(1500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            return null
        }
    }
}