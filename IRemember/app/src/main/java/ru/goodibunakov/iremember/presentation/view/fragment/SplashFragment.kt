package ru.goodibunakov.iremember.presentation.view.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.goodibunakov.iremember.BuildConfig
import ru.goodibunakov.iremember.R
import ru.goodibunakov.iremember.databinding.FragmentSplashBinding
import ru.goodibunakov.iremember.presentation.presenter.SplashFragmentPresenter

class SplashFragment : MvpAppCompatFragment(R.layout.fragment_splash), SplashFragmentVew,
    MyAnimationListener {

    private var splashIn: Animation? = null
    private var splashOut: Animation? = null

    private val binding by viewBinding(FragmentSplashBinding::bind)

    @InjectPresenter
    lateinit var splashFragmentPresenter: SplashFragmentPresenter

    @ProvidePresenter
    fun providePresenter(): SplashFragmentPresenter {
        return SplashFragmentPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.version.text =
            String.format(resources.getString(R.string.version), BuildConfig.VERSION_NAME)

        splashIn = AnimationUtils.loadAnimation(activity, R.anim.splash_anim_in)
        splashOut = AnimationUtils.loadAnimation(activity, R.anim.splash_anim_out)

        splashIn?.setAnimationListener(this)
        splashOut?.setAnimationListener(this)

        binding.root.startAnimation(splashIn)
        splashOut?.startOffset = 1000
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