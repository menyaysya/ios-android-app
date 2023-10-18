package com.vdt.testone

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
fun View.swap(time: Long, function: () -> Unit) {
    this.animate()
        .scaleX(0f)
        .scaleY(0f)

        .withEndAction {
            function()
            this.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(AccelerateDecelerateInterpolator()).duration = time / 2
        }
        .setInterpolator(AccelerateDecelerateInterpolator()).duration = time / 2

}
fun replaceFragment(
    fragment: Fragment,
    addStack: Boolean = true,
    animationIn: Int = 0,
    animationOut: Int = 0
) {

    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .setCustomAnimations(animationIn, animationOut)
            .addToBackStack(null)
            .replace(R.id.dataContainer, fragment)
            .commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .setCustomAnimations(animationIn, animationOut)
            .replace(R.id.dataContainer, fragment)
            .commit()
    }
}