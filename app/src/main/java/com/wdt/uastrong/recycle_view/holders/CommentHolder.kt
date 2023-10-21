package com.wdt.uastrong.recycle_view.holders

import com.wdt.uastrong.recycle_view.views.CommentView


interface CommentHolder {
    fun drawComment(view: CommentView)
    fun onAttach(view:CommentView)
    fun onDettach()

}