package com.wdt.uastrong.base


import androidx.fragment.app.Fragment
import com.wdt.uastrong.utils.APP_ACTIVITY

open class BaseFragment(layout:Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.onStopLoading()
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.onStopLoading()
    }
}