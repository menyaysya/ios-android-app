package com.wdt.uastrong.recycle_view.views


import com.wdt.uastrong.models.CommonModel
import com.wdt.uastrong.utils.TYPE_COMMENT
import com.wdt.uastrong.utils.TYPE_FEED_DEFAULT

class AppViewCommentFactory {
    companion object {
        fun getView(comment: CommonModel): CommentView {
            return when (comment.type) {
                TYPE_COMMENT -> {
                    ViewComment(
                        comment.id,
                        comment.from,
                        comment.newsID,
                        comment.timeStamp.toString(),
                        comment.fileUrl,
                        comment.title,
                        comment.description,
                        comment.likeCount
                    )
                }
                TYPE_FEED_DEFAULT -> {
                    ViewCommentHeader(
                        comment.id,
                        comment.from,
                        comment.newsID,
                        comment.timeStamp.toString(),
                        comment.fileUrl,
                        comment.title,
                        comment.description,
                        comment.likeCount

                    )
                }

                else -> {
                    ViewComment(
                        comment.id,
                        comment.from,
                        comment.newsID,
                        comment.timeStamp.toString(),
                        comment.title,
                        comment.description,
                        comment.likeCount
                    )
                }
            }
        }
    }
}