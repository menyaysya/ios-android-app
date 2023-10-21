package com.wdt.uastrong.recycle_view.views

import com.wdt.uastrong.models.CommonModel
import com.wdt.uastrong.utils.TYPE_FEED_DEFAULT

class AppViewFeedFactory {
    companion object {
        fun getView(feed: CommonModel): FeedView {
            return when (feed.type) {
                TYPE_FEED_DEFAULT -> {
                    ViewFeedDefault(
                        feed.id,
                        feed.from,
                        feed.timeStamp.toString(),
                        feed.fileUrl,
                        feed.title,
                        feed.description,
                        feed.likeCount,
                        feed.commentCount.toString()
                    )
                }

                else -> {
                    ViewFeedDefault(
                        feed.id,
                        feed.from,
                        feed.timeStamp.toString(),
                        feed.fileUrl,
                        feed.title,
                        feed.description,
                        feed.likeCount,
                        feed.commentCount.toString()
                    )
                }
            }
        }
    }
}