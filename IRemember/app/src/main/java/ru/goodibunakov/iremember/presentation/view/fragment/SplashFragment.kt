package ru.goodibunakov.iremember.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.fragment_splash.view.*
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.BuildConfig
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.presentation.presenter.SplashFragmentPresenter

class SplashFragment : MvpAppCompatFragment(), SplashFragmentVew, MyAnimationListener {

    private var splashIn: Animation? = null
    private var splashOut: Animation? = null

    @InjectPresenter
    lateinit var splashFragmentPresenter: SplashFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): SplashFragmentPresenter {
        return SplashFragmentPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_splash, container, false)
        view.version.text =
            String.format(resources.getString(R.string.version), BuildConfig.VERSION_NAME)

        splashIn = AnimationUtils.loadAnimation(activity, R.anim.splash_anim_in)
        splashOut = AnimationUtils.loadAnimation(activity, R.anim.splash_anim_out)

        splashIn?.setAnimationListener(this)
        splashOut?.setAnimationListener(this)

        container?.startAnimation(splashIn)
        splashOut?.startOffset = 1000

        return view
    }

    override fun close() {
        if (activity != null) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .remove(this@SplashFragment)
                .commitAllowingStateLoss()
        }
    }

    override fun onAnimationEnd(animation: Animation) {
        if (animation === splashIn) {
            view?.startAnimation(splashOut)
        }
        if (animation === splashOut) {
            splashFragmentPresenter.haveToClose()
        }
    }
}