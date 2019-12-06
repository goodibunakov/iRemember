package ru.goodibunakov.iremember.presentation.view.fragment

import android.view.animation.Animation

interface MyAnimationListener : Animation.AnimationListener {
    override fun onAnimationRepeat(animation: Animation?) {}
    override fun onAnimationStart(animation: Animation?) {}
}