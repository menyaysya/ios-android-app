package com.wdt.uastrong.recycle_view.holders

import com.wdt.uastrong.recycle_view.views.FeedView

interface FeedHolder {
    fun drawFeed(view: FeedView)
    fun onAttach(view:FeedView)
    fun onDettach()

}