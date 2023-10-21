package com.wdt.uastrong.fragment.feed

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.R
import com.wdt.uastrong.base.BaseFragment
import com.wdt.uastrong.database.NODE_COMMENTS
import com.wdt.uastrong.database.NODE_FEEDS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.database.getCommonModel
import com.wdt.uastrong.database.sendComment
import com.wdt.uastrong.databinding.FragmentFeedCommentBinding
import com.wdt.uastrong.recycle_view.views.AppViewCommentFactory
import com.wdt.uastrong.utils.AppChildEventListener
import com.wdt.uastrong.utils.AppTextWatcher
import com.wdt.uastrong.utils.AppValueEventListener


class FeedCommentFragment(private val newsId: String) :
    BaseFragment(R.layout.fragment_feed_comment) {

    private val mRefNews = REF_DATABASE_ROOT.child(NODE_FEEDS).child(newsId)
    private val mRefComments = REF_DATABASE_ROOT.child(NODE_COMMENTS).child(newsId)

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAdapter: FeedCommentAdapter


    private lateinit var mCommentsListener: AppChildEventListener
    private var mSmoothScrollToPosition = false
    private var mIsScrolling = false
    private var mCountComents = 10

    private var commentText = ""

    private lateinit var binding: FragmentFeedCommentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedCommentBinding.bind(view)
    }
    override fun onResume() {
        super.onResume()

        initFields()
        initRecyclerView()
        initComment()
    }

    private fun initFields() {

        mLayoutManager = LinearLayoutManager(this.context)


        mAdapter = FeedCommentAdapter()

        binding.commentRecycleView.adapter = mAdapter
        binding.commentRecycleView.setHasFixedSize(true)
        binding.commentRecycleView.isNestedScrollingEnabled = false
        binding.commentRecycleView.layoutManager = mLayoutManager


        mRefNews
            .addListenerForSingleValueEvent(AppValueEventListener {

                val feedHeader = it.getCommonModel()

                mAdapter.addItemToBottom(AppViewCommentFactory.getView(feedHeader)) {

                    mRefComments.limitToFirst(mCountComents)
                        .addChildEventListener(mCommentsListener)
                }
            })

        mCommentsListener = AppChildEventListener { snap, removed ->

            val comment = snap.getCommonModel()

            if (removed) {
                mAdapter.removeItem(AppViewCommentFactory.getView(comment)) {
                    //mCommentToolbar.commentLoadProgressBar.visibility = View.GONE
                }
            } else {
                mAdapter.addItemToBottom(AppViewCommentFactory.getView(comment)) {
                    if (mSmoothScrollToPosition) {
                        binding.commentRecycleView.smoothScrollToPosition(mAdapter.itemCount)
                        mSmoothScrollToPosition = false
                    }
                   // mCommentToolbar.commentLoadProgressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun initComment() {
        binding.commentInputMessage.addTextChangedListener(AppTextWatcher {
            commentText = binding.commentInputMessage.text.toString()
            if (commentText.isEmpty())
                binding.commentSendMessageBtn.visibility = View.GONE
            else
                binding.commentSendMessageBtn.visibility = View.VISIBLE
        })

        binding.commentSendMessageBtn.setOnClickListener {
            mSmoothScrollToPosition = true
            commentText = binding.commentInputMessage.text.toString()
            if (commentText.isNotEmpty())
                sendComment(commentText, newsId) {
                    binding.commentInputMessage.setText("")
                    binding.commentRecycleView.smoothScrollToPosition(mAdapter.itemCount)
                }
        }
    }

    private fun initRecyclerView() {

        binding.commentRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mIsScrolling && dy > 0 &&
                    mLayoutManager.findLastVisibleItemPosition() >= mAdapter.itemCount - 3
                )
                    updateData()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    mIsScrolling = true
            }
        })
    }

    private fun updateData() {
       // mCommentToolbar.commentLoadProgressBar.visibility = View.VISIBLE
        mSmoothScrollToPosition = true
        mIsScrolling = false
        mCountComents += 5
        mRefComments.removeEventListener(mCommentsListener)
        mRefComments.limitToFirst(mCountComents).addChildEventListener(mCommentsListener)
    }



    override fun onPause() {
        super.onPause()
       // mCommentToolbar.visibility = View.GONE
        mRefComments.removeEventListener(mCommentsListener)
        mAdapter.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.destroy()
    }
    /*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        APP_ACTIVITY.menuInflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        replaceFragment(UserChatFragment())
        return true
    }

     */
}