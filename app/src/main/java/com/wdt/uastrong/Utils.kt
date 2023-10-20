package com.wdt.uastrong

import androidx.fragment.app.Fragment

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
