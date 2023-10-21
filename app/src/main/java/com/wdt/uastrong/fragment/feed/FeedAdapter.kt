package com.wdt.uastrong.fragment.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.recycle_view.holders.AppHolderFeedFactory
import com.wdt.uastrong.recycle_view.holders.FeedHolder
import com.wdt.uastrong.recycle_view.views.FeedView

class FeedAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListFeedCache = mutableListOf<FeedView>()
    private var mListHolders = mutableListOf<FeedHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFeedFactory.getHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return mListFeedCache[position].getTypeView()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FeedHolder).drawFeed(mListFeedCache[position])
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as FeedHolder).onAttach(mListFeedCache[holder.absoluteAdapterPosition]) //adapterpos
        mListHolders.add(holder as FeedHolder)
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        mListHolders.remove(holder as FeedHolder)
        (holder as FeedHolder).onDettach()
        super.onViewDetachedFromWindow(holder)
    }
    override fun getItemCount(): Int = mListFeedCache.size

    fun addItemToBottom(
        item: FeedView,
        onSuccess: () -> Unit
    ) {
        if (!mListFeedCache.contains(item)) {
            mListFeedCache.add(item)
            notifyItemInserted(mListFeedCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(
        item: FeedView,
        onSuccess: () -> Unit
    ) {
        if (!mListFeedCache.contains(item)) {
            mListFeedCache.add(item)
            mListFeedCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }
    fun removeItem(
        item: FeedView,
        onSuccess: () -> Unit
    ) {
        var pos = -1
        mListFeedCache.forEachIndexed{index,savedItem->
            if(savedItem.id == item.id){
                pos = index
            }
        }
        if(pos != -1){
            mListFeedCache.remove(item)
            notifyItemRemoved(pos)
            onSuccess()
        }
    }
    fun clear() {
        mListFeedCache.clear()
    }
    fun destroy() {
        mListHolders.forEach {
            it.onDettach()
        }
    }

}
