package com.wdt.uastrong.fragment.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.recycle_view.holders.AppHolderCommentFactory
import com.wdt.uastrong.recycle_view.holders.CommentHolder
import com.wdt.uastrong.recycle_view.views.CommentView


class FeedCommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListCommentCache = mutableListOf<CommentView>()
    private var mListHolders = mutableListOf<CommentHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderCommentFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListCommentCache[position].getTypeView()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CommentHolder).drawComment(mListCommentCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as CommentHolder).onAttach(mListCommentCache[holder.absoluteAdapterPosition])
        mListHolders.add(holder as CommentHolder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        mListHolders.remove(holder as CommentHolder)
        (holder as CommentHolder).onDettach()
        super.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = mListCommentCache.size

    fun addItemToBottom(
        item: CommentView,
        onSuccess: () -> Unit
    ) {
        if (!mListCommentCache.contains(item)) {

            mListCommentCache.add(item)
            notifyItemInserted(mListCommentCache.size)
        }
        onSuccess()
    }

    fun removeItem(
        item: CommentView,
        onSuccess: () -> Unit
    ) {
        var pos = -1
        mListCommentCache.forEachIndexed { index, savedItem ->
            if (savedItem.id == item.id) {
                pos = index
            }
        }
        if (pos != -1) {
            mListCommentCache.remove(item)
            notifyItemRemoved(pos)
            onSuccess()
        }
    }

    fun clear() {
        mListCommentCache.clear()
    }

    fun destroy() {
        mListHolders.forEach {
            it.onDettach()
        }
    }
}