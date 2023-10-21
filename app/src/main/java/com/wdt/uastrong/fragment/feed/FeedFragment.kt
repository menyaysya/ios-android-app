package com.wdt.uastrong.fragment.feed

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.R
import com.wdt.uastrong.database.NODE_FEEDS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.database.getCommonModel
import com.wdt.uastrong.databinding.FragmentFeedBinding
import com.wdt.uastrong.recycle_view.views.AppViewFeedFactory
import com.wdt.uastrong.utils.AppChildEventListener
import com.wdt.uastrong.utils.hideKeyboard
import com.wdt.uastrong.utils.initSwipeRefresh
import timber.log.Timber

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val mRefFeeds = REF_DATABASE_ROOT.child(NODE_FEEDS)

    private lateinit var mLayoutManager: LinearLayoutManager

    private lateinit var mAdapter: FeedAdapter
    private lateinit var mFeedToolbar: View

    private lateinit var mFeedsListener: AppChildEventListener
    private var mSmoothScrollToPostition = true
    private var mIsScrolling = false
    private var mCountFeeds = 10

    private var mScrollToPos = true


    private lateinit var binding: FragmentFeedBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.bind(view)
    }
    override fun onResume() {
        super.onResume()

        initFields()
        initRecyclerView()

        mScrollToPos = true
    }

    private fun initFields() {

        setHasOptionsMenu(true)
        hideKeyboard()

        binding.feedSwipeRefresh.initSwipeRefresh()

        mLayoutManager = LinearLayoutManager(this.context)
        mLayoutManager.reverseLayout = true


        mAdapter = FeedAdapter()

        binding.feedRecycleView.adapter = mAdapter
        binding.feedRecycleView.setHasFixedSize(true)
        binding.feedRecycleView.isNestedScrollingEnabled = false
        binding.feedRecycleView.layoutManager = mLayoutManager

        mFeedsListener = AppChildEventListener { snap, removed ->

            val feed = snap.getCommonModel()
            if (removed) {
                mAdapter.removeItem(AppViewFeedFactory.getView(feed)) {
                    binding.feedSwipeRefresh.isRefreshing = false
                }
            } else {
                if (mSmoothScrollToPostition) {
                    mAdapter.addItemToBottom(AppViewFeedFactory.getView(feed)) {
                        binding.feedRecycleView.smoothScrollToPosition(mAdapter.itemCount)

                        Timber.e(mScrollToPos.toString())
                        if (mScrollToPos)
                            binding.feedRecycleView.smoothScrollToPosition(mAdapter.itemCount)
                    }
                } else {
                    mAdapter.addItemToTop(AppViewFeedFactory.getView(feed)) {
                        binding.feedSwipeRefresh.isRefreshing = false

                        Timber.e(mScrollToPos.toString())
                        if (mScrollToPos)
                            binding.feedRecycleView.smoothScrollToPosition(mAdapter.itemCount)
                    }
                }
            }
        }
    }



    private fun initRecyclerView() {


        mRefFeeds.limitToLast(mCountFeeds).addChildEventListener(mFeedsListener)
        mRefFeeds.keepSynced(true)

        binding.feedRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mIsScrolling && dy > 0 &&
                    mLayoutManager.findFirstVisibleItemPosition() <= 3
                ) {
                    mCountFeeds += 5
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    mIsScrolling = true
            }
        })

        binding.feedSwipeRefresh.setOnRefreshListener { updateData() }

        binding.feedRecycleView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && mAdapter.itemCount > 0) {
                binding.feedRecycleView.postDelayed({
                    binding.feedRecycleView.smoothScrollToPosition(
                        mAdapter.itemCount - 1
                    )
                }, 100)
            }
        }
    }

    private fun updateData() {
        mScrollToPos = false
        mSmoothScrollToPostition = false
        mIsScrolling = false
        mRefFeeds.removeEventListener(mFeedsListener)
        mRefFeeds.limitToLast(mCountFeeds).addChildEventListener(mFeedsListener)
    }



    override fun onPause() {
        super.onPause()
        mFeedToolbar.visibility = View.GONE
        mRefFeeds.removeEventListener(mFeedsListener)
        mAdapter.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.destroy()
    }

}