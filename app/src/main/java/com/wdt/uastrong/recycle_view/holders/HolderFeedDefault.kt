package com.wdt.uastrong.recycle_view.holders


import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.ablanco.zoomy.Zoomy
import com.wdt.uastrong.R
import com.wdt.uastrong.database.NODE_USERS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.database.UID
import com.wdt.uastrong.database.getNewsLikesData
import com.wdt.uastrong.database.getUserModel
import com.wdt.uastrong.database.pushLike
import com.wdt.uastrong.database.removeLike
import com.wdt.uastrong.database.removePost
import com.wdt.uastrong.fragment.feed.FeedCommentFragment
import com.wdt.uastrong.models.UserModel
import com.wdt.uastrong.recycle_view.views.FeedView
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.AppValueEventListener
import com.wdt.uastrong.utils.asDate
import com.wdt.uastrong.utils.asTime
import com.wdt.uastrong.utils.clearImageView
import com.wdt.uastrong.utils.downloadAndSetImage
import com.wdt.uastrong.utils.likeAnimation
import com.wdt.uastrong.utils.replaceFragment
import com.wdt.uastrong.utils.setLikeText
import com.wdt.uastrong.utils.showDialog
import com.wdt.uastrong.utils.showToast
import de.hdodenhof.circleimageview.CircleImageView

class HolderFeedDefault(view: View) : RecyclerView.ViewHolder(view), FeedHolder {

    private lateinit var creatorModel: UserModel
    private val feedBody: ConstraintLayout = view.findViewById(R.id.feedBody)
    private val postAvatar: CircleImageView = view.findViewById(R.id.news_item_avatar)
    private val postTitle: TextView = view.findViewById(R.id.news_item_title)
    private val postImage: ImageView = view.findViewById(R.id.news_item_image)
    private var postDescription: TextView = view.findViewById(R.id.news_item_decsription)
    private val postTime: TextView = view.findViewById(R.id.news_item_time)
    private val postDate: TextView = view.findViewById(R.id.news_item_date)

    private val postLikeBtn: Button = view.findViewById(R.id.news_item_like_btn)
    private val postLikeCount: TextView = view.findViewById(R.id.news_item_likes_count)
    private val postCommentItem: TextView = view.findViewById(R.id.news_item_comment_item)

    private val postDeleteBtn: Button = view.findViewById(R.id.news_item_delete_btn)

    private lateinit var imagePinchZoom: Zoomy.Builder

    private lateinit var likeBtnListener: Unit
    private var userLiked = false

    private var likeCount = "0"

    override fun drawFeed(view: FeedView) {
        postTitle.text = view.title
        postDescription.text = view.description
        postTime.text = view.timeStamp.asTime()
        postDate.text = view.timeStamp.asDate()

        getNewsLikesData(view.id) { rLikeCount, rUserLiked ->
            likeCount = rLikeCount
            userLiked = rUserLiked
            updateLike()
        }
        feedBody.setOnClickListener {
            replaceFragment(FeedCommentFragment(view.id))
        }

        if (view.commentCount != "0" &&
            view.commentCount.isNotEmpty()
        ) {
            postCommentItem.visibility = View.VISIBLE
            postCommentItem.text =
                APP_ACTIVITY.resources.getQuantityString(
                    R.plurals.count_comments,
                    view.commentCount.toInt(),
                    view.commentCount.toInt()
                )
        } else
            postCommentItem.visibility = View.GONE

        if (view.from == UID) {
            postDeleteBtn.visibility = View.VISIBLE
            postDeleteBtn.setOnClickListener {
                showDialog(APP_ACTIVITY.getString(R.string.questionRemoveNews)) {
                    if (it)
                        removePost(view.id) {
                            showToast(APP_ACTIVITY.getString(R.string.answerNewsDeleted))
                        }
                }
            }
        } else {
            postDeleteBtn.visibility = View.GONE
        }

        REF_DATABASE_ROOT.child(NODE_USERS).child(view.from)
            .addValueEventListener(AppValueEventListener {
                creatorModel = it.getUserModel()

                if (creatorModel.fullname.isEmpty())
                    postTitle.text = "Анонім"
                else
                    postTitle.text = creatorModel.fullname

                postAvatar.downloadAndSetImage(creatorModel.photoUrl, true)
            })

        if (view.fileUrl == "empty") {
            postImage.clearImageView()
        } else {
            postImage.downloadAndSetImage(view.fileUrl, false)
            postImage.visibility = View.VISIBLE

            imagePinchZoom = Zoomy.Builder(APP_ACTIVITY).target(postImage)
            imagePinchZoom.register()
        }

        likeBtnListener = postLikeBtn.setOnClickListener {

            if (userLiked) {
                postLikeBtn.likeAnimation(false) {
                    likeCount = (likeCount.toInt() - 1).toString()
                    postLikeCount.setLikeText(likeCount)
                }
                userLiked = false
                removeLike(view.id) {}
            } else {
                postLikeBtn.likeAnimation(true) {
                    likeCount = (likeCount.toInt() + 1).toString()
                    postLikeCount.setLikeText(likeCount)
                }
                userLiked = true
                pushLike(view.id) {}
            }
        }
    }

    private fun updateLike() {
        postLikeBtn.likeAnimation(userLiked) {}
        postLikeCount.setLikeText(likeCount)
    }

    override fun onAttach(view: FeedView) {

    }

    override fun onDettach() {
        //likeBtnListener
        //if (::imagePinchZoom.isInitialized)
        //   Zoomy.unregister(postImage)
    }
}