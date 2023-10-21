package com.wdt.uastrong.recycle_view.views

data class ViewFeedDefault(
    override val id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val title: String = "",
    override val description: String = "",
    override val likeCount: String = "",
    override val commentCount: String

) : FeedView {
    override fun getTypeView(): Int {
        return FeedView.TYPE_FEED_DEFAULT
    }

    override fun equals(other: Any?): Boolean {
        return (other as FeedView).id == id
    }
}