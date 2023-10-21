package com.wdt.uastrong

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.wdt.uastrong.database.AUTH
import com.wdt.uastrong.database.initFirebase
import com.wdt.uastrong.database.initUser
import com.wdt.uastrong.fragment.auth_fragments.LoginFragment
import com.wdt.uastrong.fragment.feed.FeedFragment
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.AppStates
import com.wdt.uastrong.utils.replaceFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        APP_ACTIVITY = this

        Timber.plant(Timber.DebugTree())
        initFirebase()

        initUser { initFunc() }

    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    fun onStartLoading() {
        findViewById<FrameLayout>(R.id.loadingLayout).visibility = View.VISIBLE
    }

    fun onStopLoading() {
        findViewById<FrameLayout>(R.id.loadingLayout).visibility = View.GONE
    }




    private fun initFunc() {
        if (AUTH.currentUser != null)
            replaceFragment(FeedFragment(), false)
         else
            replaceFragment(LoginFragment(), false)
    }




}