package com.wdt.uastrong.recycle_view.holders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wdt.uastrong.R
import com.wdt.uastrong.database.NODE_USERS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.database.UID
import com.wdt.uastrong.database.commentPushLike
import com.wdt.uastrong.database.commentRemoveLike
import com.wdt.uastrong.database.getCommentsLikesData
import com.wdt.uastrong.database.getUserModel
import com.wdt.uastrong.database.removeComment
import com.wdt.uastrong.models.UserModel
import com.wdt.uastrong.recycle_view.views.CommentView
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.AppValueEventListener
import com.wdt.uastrong.utils.asDate
import com.wdt.uastrong.utils.asTime
import com.wdt.uastrong.utils.downloadAndSetImage
import com.wdt.uastrong.utils.likeAnimation
import com.wdt.uastrong.utils.setLikeText
import com.wdt.uastrong.utils.showDialog
import com.wdt.uastrong.utils.showToast

import de.hdodenhof.circleimageview.CircleImageView


class HolderComment(view: View) : RecyclerView.ViewHolder(view), CommentHolder {

    private lateinit var creatorModel: UserModel

    private val commentAvatar: CircleImageView = view.findViewById(R.id.comment_item_avatar)
    private val commentTitle: TextView = view.findViewById(R.id.comment_item_title)
    private var commentDescription: TextView = view.findViewById(R.id.comment_item_decsription)
    private val commentTime: TextView = view.findViewById(R.id.comment_item_time)
    private val commentDate: TextView = view.findViewById(R.id.comment_item_date)
    private val commentLikeBtn: Button = view.findViewById(R.id.comment_item_like_btn)
    private val commentLikeCount: TextView = view.findViewById(R.id.comment_item_likes_count)
    private val commentDeleteBtn: Button = view.findViewById(R.id.comment_item_delete_btn)

    private lateinit var likeBtnListener: Unit
    private var userLiked = false

    private var likeCount = "0"

    override fun drawComment(view: CommentView) {

        commentTitle.text = view.title
        commentDescription.text = view.description
        commentTime.text = view.timeStamp.asTime()
        commentDate.text = view.timeStamp.asDate()

        getCommentsLikesData(view.id) { rLikeCount, rUserLiked ->
            likeCount = rLikeCount
            userLiked = rUserLiked
            updateLike()
        }

        if (view.from == UID) {
            commentDeleteBtn.visibility = View.VISIBLE
            commentDeleteBtn.setOnClickListener {
                showDialog(APP_ACTIVITY.getString(R.string.removeCommentQuestion)) {
                    if (it)
                        removeComment(view.id, view.newsID) {
                            showToast(APP_ACTIVITY.getString(R.string.commentRemovedAnswer))
                        }
                }
            }
        } else commentDeleteBtn.visibility = View.GONE


        REF_DATABASE_ROOT.child(NODE_USERS).child(view.from)
            .addValueEventListener(AppValueEventListener {
                creatorModel = it.getUserModel()

                if (creatorModel.fullname.isEmpty())
                    commentTitle.text = "Анонім"
                else
                    commentTitle.text = creatorModel.fullname

                commentAvatar.downloadAndSetImage(creatorModel.photoUrl, true)
            })

        likeBtnListener = commentLikeBtn.setOnClickListener {

            if (userLiked) {
                commentLikeBtn.likeAnimation(false) {
                    likeCount = (likeCount.toInt() - 1).toString()
                    commentLikeCount.setLikeText(likeCount)
                }
                userLiked = false
                commentRemoveLike(view.id) {}
            } else {
                commentLikeBtn.likeAnimation(true) {
                    likeCount = (likeCount.toInt() + 1).toString()
                    commentLikeCount.setLikeText(likeCount)
                }
                userLiked = true
                commentPushLike(view.id) {}
            }
        }
    }

    private fun updateLike() {
        commentLikeBtn.likeAnimation(userLiked) {}
        commentLikeCount.setLikeText(likeCount)
    }

    override fun onAttach(view: CommentView) {

    }

    override fun onDettach() {

    }
}