package com.wdt.uastrong.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.wdt.uastrong.MainActivity
import com.wdt.uastrong.R
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

fun getStr(key: Int): String {
    return(APP_ACTIVITY.resources.getString(key))
}
fun getDraw(key: Int): Drawable {
    return (ContextCompat.getDrawable(APP_ACTIVITY, key)!!)
}

fun getCol(key: Int): Int {
    return (ContextCompat.getColor(APP_ACTIVITY, key))
}


fun showToast(msg: String) {
    Toast.makeText(APP_ACTIVITY, msg, Toast.LENGTH_LONG).show()
}

fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}


fun restartActivity() {

    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.finish()
    APP_ACTIVITY.startActivity(intent)
}

fun SwipeRefreshLayout.initSwipeRefresh() {
    this.setProgressBackgroundColorSchemeColor(
        ContextCompat.getColor(APP_ACTIVITY, R.color.swipe_refresh_color_bg)
    )
    this.setColorSchemeColors(
        ContextCompat.getColor(APP_ACTIVITY, R.color.swipe_refresh_color_A),
        ContextCompat.getColor(APP_ACTIVITY, R.color.swipe_refresh_color_B)
    )
}



fun String.asTime(): String {
    return if (this != "") {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        var normalizeString = timeFormat.format(time)

        if (normalizeString.startsWith("0"))
            normalizeString = normalizeString.substring(1, normalizeString.length)

        normalizeString
    } else {
        "--:--"
    }

}


fun String.asDate(): String {

    return if (this != "") {
        val locate = Locale.getDefault()
        val time = Date(this.toLong())
        val timeNow = Date()
        val timeFormat = SimpleDateFormat("yyyy-MM-dd", locate)

        val inDate = timeFormat.format(time)
        val nowDate = timeFormat.format(timeNow)

        val timeFormatWithoutDay = SimpleDateFormat("yyyy-MM", locate)
        val timeFormatOnlyDay = SimpleDateFormat("dd", locate)

        val timeFormatDD = SimpleDateFormat("dd", locate)
        val timeFormatMM = SimpleDateFormat("MM", locate)


        return if (inDate == nowDate)
            APP_ACTIVITY.getString(R.string.today)
        else if (timeFormatWithoutDay.format(time) ==
            timeFormatWithoutDay.format(timeNow)
        ) {
            if (timeFormatOnlyDay.format(time).toInt() + 1 ==
                (timeFormatOnlyDay.format(timeNow).toInt())
            )
                APP_ACTIVITY.getString(R.string.yesterday)
            else {
                var monthString = timeFormatDD.format(time).toString() + " "
                if (monthString.startsWith("0"))
                    monthString = monthString.substring(1, monthString.length)

                when (timeFormatMM.format(time).toInt()) {
                    1 -> monthString + "січня"
                    2 -> monthString + "лютого"
                    3 -> monthString + "березня"
                    4 -> monthString + "квітня"
                    5 -> monthString + "травня"
                    6 -> monthString + "червня"
                    7 -> monthString + "липня"
                    8 -> monthString + "серпня"
                    9 -> monthString + "вересня"
                    10 -> monthString + "жовтня"
                    11 -> monthString + "листопада"
                    12 -> monthString + "грудня"

                    else -> {
                        ""
                    }
                }
            }
        } else {
            timeFormat.format(time).toString()
        }
    } else {
        ""
    }
}



fun showDialog(question: String, function: (answer: Boolean) -> Unit) {

    val builder: AlertDialog.Builder = AlertDialog.Builder(APP_ACTIVITY)
    builder.setMessage(question)
    builder.setCancelable(true)

    builder.setPositiveButton(
        "Так"
    ) { sDialog, _ ->
        sDialog.cancel()
        function(true)
    }

    builder.setNegativeButton(
        "Ні"
    ) { sDialog, _ ->
        sDialog.cancel()
        function(false)
    }
    builder.create().show()
}



fun ImageView.downloadAndSetImage(url: String, isAvatar: Boolean = true) {
    if (url.isNotEmpty() && url != "empty") {
        if (isAvatar) {
            Glide
                .with(APP_ACTIVITY)
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.baseline_image_24)
                .into(this)

        } else {
            Glide
                .with(APP_ACTIVITY)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.baseline_image_24)
                .into(this)
        }
    }
}

fun ImageView.clearImageView() {
    this.setImageResource(0)
    this.setImageDrawable(null)
    this.setImageURI(null)
}




fun TextView.setLikeText(likeCount: String) {
    if (likeCount == "0" ||
        likeCount == ""
    ) this.visibility = View.GONE
    else {
        this.visibility = View.VISIBLE
        this.text = likeCount
    }
}

fun Button.setLikeImage(userLiked: Boolean) {
    if (userLiked) {
            this.setBackgroundResource(R.drawable.ic_like_cheked)
        this.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                APP_ACTIVITY,
                R.color.liked_color
            )
        )
    } else {
        this.setBackgroundResource(R.drawable.ic_like_uncheck)
        this.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                APP_ACTIVITY,
                R.color.white_light
            )
        )
    }
}
fun Button.likeAnimation(likedUp: Boolean, function: () -> Unit) {

    val fadeOut: Animation = AnimationUtils.loadAnimation(APP_ACTIVITY, R.anim.fadeout)
    val fadeIn: Animation = AnimationUtils.loadAnimation(APP_ACTIVITY, R.anim.fadein)

    if (likedUp) {
        this.startAnimation(fadeOut)
        fadeOut.setAnimationListener(AppAnimationEndListener {
            this.setLikeImage(true)
            this.startAnimation(fadeIn)
            fadeIn.setAnimationListener(AppAnimationEndListener { function() })
        })
    } else {
        this.startAnimation(fadeOut)
        fadeOut.setAnimationListener(AppAnimationEndListener {
            this.setLikeImage(false)
            this.startAnimation(fadeIn)
            fadeIn.setAnimationListener(AppAnimationEndListener { function() })
        })
    }

}


fun cropImage(fragment: Fragment, sourceUri: Uri, circleImage: Boolean = false) {
    val destinationUri: Uri =
        Uri.fromFile(File(APP_ACTIVITY.cacheDir, "IMG_" + System.currentTimeMillis()))

    val options = UCrop.Options()
    options.setCompressionQuality(80)

    options.setToolbarColor(ContextCompat.getColor(APP_ACTIVITY, R.color.main_bg_end))
    options.setStatusBarColor(ContextCompat.getColor(APP_ACTIVITY, R.color.main_bg_end))
    options.setToolbarWidgetColor(
        ContextCompat.getColor(
            APP_ACTIVITY,
            R.color.white_light
        )
    )
    options.setActiveControlsWidgetColor(
        ContextCompat.getColor(
            APP_ACTIVITY,
            R.color.white_light
        )
    )

    options.setRootViewBackgroundColor(ContextCompat.getColor(APP_ACTIVITY, R.color.main_bg_start))
    options.setDimmedLayerColor(ContextCompat.getColor(APP_ACTIVITY, R.color.main_bg_start))
    options.setHideBottomControls(true)
    options.setCircleDimmedLayer(circleImage)
    options.setToolbarTitle("Редагування фото")
    options.withAspectRatio(1F, 1F)
    options.withMaxResultSize(720, 720)

    UCrop.of(sourceUri, destinationUri)
        .withOptions(options)
        .start(APP_ACTIVITY, fragment, UCrop.REQUEST_CROP)
}
