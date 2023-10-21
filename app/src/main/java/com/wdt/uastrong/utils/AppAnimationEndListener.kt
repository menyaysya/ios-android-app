package com.wdt.uastrong.utils


import android.view.animation.Animation


class AppAnimationEndListener(val onEnd: () -> Unit) : Animation.AnimationListener {
    override fun onAnimationStart(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        onEnd()
    }

    override fun onAnimationRepeat(p0: Animation?) {

    }

}