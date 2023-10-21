package com.wdt.uastrong.recycle_view.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.R
import com.wdt.uastrong.recycle_view.views.CommentView

class AppHolderCommentFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                CommentView.TYPE_COMMENT -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.comment_item, parent, false)
                    HolderComment(view)
                }
                CommentView.TYPE_FEED_DEFAULT -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.comment_header_item, parent, false)
                    HolderCommentHeader(view)
                }

                else ->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.comment_item, parent, false)
                    HolderComment(view)
                }
            }
        }
    }
}