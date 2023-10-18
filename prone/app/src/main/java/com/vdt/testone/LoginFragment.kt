package com.vdt.testone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import timber.log.Timber

class LoginFragment : Fragment(R.layout.fragment_login) {

    var num = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var buttonvalue = view.findViewById<FrameLayout>(R.id.buttonTest)
        buttonvalue.setOnClickListener {
            num++
            it.swap(300) {
                view.findViewById<TextView>(R.id.buttonText).text = num.toString()
                Timber.e("ButtonActiveted")
            }
        }
    }
}