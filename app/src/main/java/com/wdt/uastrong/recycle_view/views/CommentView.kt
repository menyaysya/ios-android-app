package com.wdt.uastrong.recycle_view.views

interface CommentView {
    val id: String
    val from: String
    val newsID:String
    val timeStamp: String
    val fileUrl: String
    val title: String
    val description: String
    val likeCount:String


    companion object {
        val TYPE_COMMENT: Int
            get() = 0
        val TYPE_FEED_DEFAULT: Int
            get() = 1

    }
    fun getTypeView(): Int
}