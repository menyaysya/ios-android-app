package com.wdt.uastrong.recycle_view.views

data class ViewComment(
    override val id: String,
    override val from: String,
    override val newsID:String,
    override val timeStamp: String,
    override val fileUrl:String,
    override val title: String = "",
    override val description: String = "",
    override val likeCount: String = ""

) : CommentView {
    override fun getTypeView(): Int {
        return CommentView.TYPE_COMMENT
    }

    override fun equals(other: Any?): Boolean {
        return (other as CommentView).id == id
    }
}