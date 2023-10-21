package com.wdt.uastrong.recycle_view.holders


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.R
import com.wdt.uastrong.recycle_view.views.FeedView


class AppHolderFeedFactory {
    companion object {
        fun getHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                FeedView.TYPE_FEED_DEFAULT -> {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_item, parent, false)
                    HolderFeedDefault(view)
                }
                else ->{
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_item, parent, false)
                    HolderFeedDefault(view)
                }
            }
        }
    }
}