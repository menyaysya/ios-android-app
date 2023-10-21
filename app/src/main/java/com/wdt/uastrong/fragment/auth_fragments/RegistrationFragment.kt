package com.wdt.uastrong.fragment.auth_fragments

import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.wdt.uastrong.R
import com.wdt.uastrong.database.AUTH
import com.wdt.uastrong.database.CHILD_EMAIL
import com.wdt.uastrong.database.CHILD_FULLNAME
import com.wdt.uastrong.database.CHILD_ID
import com.wdt.uastrong.database.CHILD_USERNAME
import com.wdt.uastrong.database.NODE_USERNAMES
import com.wdt.uastrong.database.NODE_USERS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.databinding.FragmentRegistrationBinding
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.hideKeyboard
import com.wdt.uastrong.utils.replaceFragment
import com.wdt.uastrong.utils.restartActivity
import com.wdt.uastrong.utils.showToast
import timber.log.Timber

class RegistrationFragment : Fragment(R.layout.fragment_registration) {


    private lateinit var binding: FragmentRegistrationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegistrationBinding.bind(view)


        binding.registerButton.setOnClickListener { registerUser() }
        binding.loginButton.setOnClickListener { replaceFragment(LoginFragment(),false) }
    }


    private fun registerUser() {
        hideKeyboard()
        val email = binding.registrationEmail.text.toString().trim { it <= ' ' }
        //val name = registeration_name.text.toString().trim { it <= ' ' }
        //val surname = registeration_surname.text.toString().trim { it <= ' ' }
        //val nickname = registeration_nickname.text.toString().trim { it <= ' ' }.lowercase()

        val name = " "
        val surname = " "
        val nickname = " "


        val password = binding.registrationPass .text.toString().trim { it <= ' ' }
        val passwordConfirm = binding.registrationPassConfirm.text.toString().trim { it <= ' ' }

        if (email.isEmpty()) {
            binding.registrationEmail.error = resources.getString(R.string.ErrorText_Email)
            binding.registrationEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            binding.registrationPass.error = resources.getString(R.string.ErrorText_Password)
            binding.registrationPass.requestFocus()
            return
        }
        if (passwordConfirm.isEmpty()) {
            binding.registrationPassConfirm.error = resources.getString(R.string.ErrorText_Password_Confirm)
            binding.registrationPassConfirm.requestFocus()
            return
        }

        if (password != passwordConfirm) {
            binding.registrationPassConfirm.error = resources.getString(R.string.ErrorText_Passwords_Not_Compare)
            binding.registrationPassConfirm.requestFocus()
            return
        }
        /*
        if (name.isEmpty()) {
            registeration_name.error = resources.getString(R.string.ErrorText_Empty)
            registeration_name.requestFocus()
            return
        }
        if (surname.isEmpty()) {
            registeration_surname.error = resources.getString(R.string.ErrorText_Empty)
            registeration_surname.requestFocus()
            return
        }
        if (nickname.isEmpty()) {
            registeration_nickname.error = resources.getString(R.string.ErrorText_Empty)
            registeration_nickname.requestFocus()
            return
        }

         */
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registrationEmail.error = resources.getString(R.string.ErrorText_EmailNotCorrect)
            binding.registrationEmail.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.registrationPass.error = resources.getString(R.string.ErrorText_PasswordShort)
            binding.registrationPass.requestFocus()
            return
        }

        APP_ACTIVITY.onStartLoading()
/*
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(nickname)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    registeration_nickname.error =
                        getString(R.string.input_error_not_unique_nickname)
                    APP_ACTIVITY.onStopLoading()
                } else {
                    */

                    createUser(
                        email,
                        password,
                        "$name $surname",
                        nickname
                    )


                    /*
                }
            }*/

        /*
        FirebaseFirestore.getInstance().collection(NODE_USERNAMES)
            .document(nickname).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result?.exists() == true) {
                        settings_input_username.error =
                            getString(R.string.input_error_not_unique_nickname)
                        APP_ACTIVITY.onStopLoading()
                    } else {
                        createUser(
                            email,
                            password,
                            "$name $surname",
                            nickname
                        )

                    }
                }
            }

         */


    }

    private fun createUser(
        email: String,
        pass: String,
        fullname: String,
        nickname: String
    ) {
        APP_ACTIVITY.onStartLoading()
        AUTH.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid
                    Timber.e("USER CREATED!")

                    showToast(resources.getString(R.string.registration_complete))
                    APP_ACTIVITY.onStopLoading()
                    restartActivity()

                    /*
                    val dateMap: MutableMap<String, Any> = mutableMapOf()
                    dateMap[CHILD_ID] = uid
                    dateMap[CHILD_FULLNAME] = fullname
                    dateMap[CHILD_USERNAME] = nickname
                    dateMap[CHILD_EMAIL] = email

                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid).setValue(dateMap)
                        .addOnSuccessListener {

                            Timber.e("USER CREATED!")
                            REF_DATABASE_ROOT.child(NODE_USERNAMES).child(nickname)
                                .setValue(mapOf(CHILD_ID to uid))
                                .addOnSuccessListener {

                                }
                                .addOnFailureListener {
                                    showToast(resources.getString(R.string.Error))
                                    APP_ACTIVITY.onStopLoading()
                                }


                        }
                        .addOnFailureListener {
                            showToast(resources.getString(R.string.Error))
                            APP_ACTIVITY.onStopLoading()
                        }

                     */
                }
            }
    }
}