package com.wdt.uastrong.fragment.auth_fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.wdt.uastrong.R
import com.wdt.uastrong.database.AUTH
import com.wdt.uastrong.databinding.FragmentLoginBinding
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.AppStates
import com.wdt.uastrong.utils.replaceFragment
import com.wdt.uastrong.utils.restartActivity
import com.wdt.uastrong.utils.showToast


class LoginFragment: Fragment(R.layout.fragment_login) {


    private lateinit var binding: FragmentLoginBinding

    private   var passVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        binding.loginButton.setOnClickListener { userLogin() }
        binding.registrationButton.setOnClickListener { replaceFragment(RegistrationFragment()) }
         binding.forgotPass.setOnClickListener { replaceFragment(ForgotPasswordFragment()) }



        binding.showPassButton.setOnClickListener {

            if(passVisible)
                binding.loginPass.transformationMethod = HideReturnsTransformationMethod.getInstance()

              else
                binding.loginPass.transformationMethod = PasswordTransformationMethod.getInstance()

            binding.loginPass.setSelection(binding.loginPass.length())
            passVisible = !passVisible

        }
    }


    private fun userLogin() {

        val email = binding.loginEmail.text.toString().trim { it <= ' ' }
        val password = binding.loginPass.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            binding.loginEmail.error = resources.getString(R.string.ErrorText_Email)
            binding.loginEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.loginPass.error = resources.getString(R.string.ErrorText_Password)
            binding.loginPass.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginEmail.error = resources.getString(R.string.ErrorText_EmailNotCorrect)
            binding.loginEmail.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.loginPass.error = resources.getString(R.string.ErrorText_PasswordShort)
            binding.loginPass.requestFocus()
            return
        }
        //Show Load Pop
        APP_ACTIVITY.onStartLoading()

        AUTH.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user!!.isEmailVerified) {
                    restartActivity()
                    AppStates.updateState(AppStates.ONLINE)
                } else {
                    user.sendEmailVerification()
                    showToast(resources.getString(R.string.EmailProblem))
                }
                APP_ACTIVITY.onStopLoading()

            } else {
                showToast(resources.getString(R.string.LoginError))
                APP_ACTIVITY.onStopLoading()
            }
        }
    }
}