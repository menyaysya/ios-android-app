package com.wdt.uastrong.fragment.feed

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.wdt.uastrong.R
import com.wdt.uastrong.base.BaseFragment
import com.wdt.uastrong.database.USER
import com.wdt.uastrong.database.createNewsInDatabase
import com.wdt.uastrong.databinding.FragmentCreatePostBinding
import com.wdt.uastrong.databinding.FragmentFeedCommentBinding
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.cropImage
import com.wdt.uastrong.utils.downloadAndSetImage
import com.wdt.uastrong.utils.hideKeyboard
import com.wdt.uastrong.utils.replaceFragment
import com.wdt.uastrong.utils.showToast
import com.yalantis.ucrop.UCrop


class CreatePostFragment : BaseFragment(R.layout.fragment_create_post) {

    private var postPhotoUrl: Uri = Uri.EMPTY

    private lateinit var binding: FragmentCreatePostBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreatePostBinding.bind(view)
    }
    override fun onResume() {
        super.onResume()

        hideKeyboard()

        binding.createPostTitle.text = USER.fullname
        binding.createPostAvatar.downloadAndSetImage(USER.photoUrl)

        binding.createPostAddPhoto.setOnClickListener { addPhoto() }
        binding.createPostButton.setOnClickListener {
            val postDescription = binding.createPostDescription.text.toString()

            if (postPhotoUrl == Uri.EMPTY && postDescription.isEmpty()) {
                showToast(getString(R.string.error_empty_post))
            } else {
                APP_ACTIVITY.onStartLoading()

                createNewsInDatabase(postDescription, postPhotoUrl) {
                    APP_ACTIVITY.onStopLoading()
                    replaceFragment(FeedFragment())
                }
            }

        }

    }




    private fun addPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    cropImage(this, it, false)
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP && data != null) {
            postPhotoUrl = UCrop.getOutput(data)!!
            binding.createPostImage.visibility = View.VISIBLE
            binding.createPostImage.setImageURI(postPhotoUrl)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            showToast(getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
    }


}