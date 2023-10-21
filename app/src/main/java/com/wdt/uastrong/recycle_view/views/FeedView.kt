package com.wdt.uastrong.recycle_view.views

interface FeedView {
    val id: String
    val from: String
    val timeStamp: String
    val fileUrl: String
    val title: String
    val description: String
    val likeCount:String
    val commentCount:String

    companion object {
        val TYPE_FEED_DEFAULT: Int
            get() = 0

    }
    fun getTypeView(): Int
}