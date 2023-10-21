package com.wdt.uastrong.fragment.auth_fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.wdt.uastrong.R
import com.wdt.uastrong.database.AUTH
import com.wdt.uastrong.databinding.FragmentForgotPasswordBinding
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.showToast

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {



    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentForgotPasswordBinding.bind(view)

        AUTH = FirebaseAuth.getInstance()
        binding.sendEmailButton.setOnClickListener { resetPassword() }
    }


    private fun resetPassword() {
        val email = binding.email.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            binding.email.error = resources.getString(R.string.ErrorText_Email)
            binding.email.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.error = resources.getString(R.string.ErrorText_EmailNotCorrect)
            binding.email.requestFocus()
            return
        }
        APP_ACTIVITY.onStartLoading()
        AUTH.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful)
                showToast(resources.getString(R.string.EmailSent))
            else
                showToast(resources.getString(R.string.Error))

            APP_ACTIVITY.onStopLoading()
        }
    }
}