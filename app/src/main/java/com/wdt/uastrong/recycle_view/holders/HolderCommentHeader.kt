package com.wdt.uastrong.recycle_view.holders

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ablanco.zoomy.Zoomy
import com.wdt.uastrong.R
import com.wdt.uastrong.database.NODE_USERS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.database.getNewsLikesData
import com.wdt.uastrong.database.getUserModel
import com.wdt.uastrong.database.pushLike
import com.wdt.uastrong.database.removeLike
import com.wdt.uastrong.models.UserModel
import com.wdt.uastrong.recycle_view.views.CommentView
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.utils.AppValueEventListener
import com.wdt.uastrong.utils.asDate
import com.wdt.uastrong.utils.asTime
import com.wdt.uastrong.utils.clearImageView
import com.wdt.uastrong.utils.downloadAndSetImage
import com.wdt.uastrong.utils.likeAnimation
import com.wdt.uastrong.utils.setLikeText
import de.hdodenhof.circleimageview.CircleImageView


class HolderCommentHeader(view: View) : RecyclerView.ViewHolder(view), CommentHolder {

    private lateinit var creatorModel: UserModel

    private val commentHeaderAvatar: CircleImageView = view.findViewById(R.id.comment_header_item_avatar)
    private val commentHeaderTitle: TextView = view.findViewById(R.id.comment_header_item_title)
    private val commentHeaderImage: ImageView = view.findViewById(R.id.comment_header_item_image)
    private var commentHeaderDescription: TextView = view.findViewById(R.id.comment_header_item_decsription)
    private val commentHeaderTime: TextView = view.findViewById(R.id.comment_header_item_time)
    private val commentHeaderDate: TextView = view.findViewById(R.id.comment_header_item_date)
    private val commentHeaderLikeBtn: Button = view.findViewById(R.id.comment_header_item_like_btn)
    private val commentHeaderLikeCount: TextView = view.findViewById(R.id.comment_header_item_likes_count)

    private lateinit var imagePinchZoom: Zoomy.Builder

    private lateinit var likeBtnListener: Unit
    private var userLiked = false

    private var likeCount = "0"


    override fun drawComment(view: CommentView) {

        commentHeaderTitle.text = view.title
        commentHeaderDescription.text = view.description
        commentHeaderTime.text = view.timeStamp.asTime()

        commentHeaderDate.text = view.timeStamp.asDate()

        getNewsLikesData(view.id) { rLikeCount, rUserLiked ->
            likeCount = rLikeCount
            userLiked = rUserLiked
            updateLike()
        }

        REF_DATABASE_ROOT.child(NODE_USERS).child(view.from)
            .addValueEventListener(AppValueEventListener {
                creatorModel = it.getUserModel()

                if (creatorModel.fullname.isEmpty())
                    commentHeaderTitle.text = "Анонім"
                else
                    commentHeaderTitle.text = creatorModel.fullname

                commentHeaderAvatar.downloadAndSetImage(creatorModel.photoUrl, true)
            })

        if (view.fileUrl == "empty") {
            commentHeaderImage.clearImageView()
        } else {
            commentHeaderImage.downloadAndSetImage(view.fileUrl, false)
            commentHeaderImage.visibility = View.VISIBLE

            imagePinchZoom = Zoomy.Builder(APP_ACTIVITY).target(commentHeaderImage)
            imagePinchZoom.register()
        }

        likeBtnListener = commentHeaderLikeBtn.setOnClickListener {

            if (userLiked) {
                commentHeaderLikeBtn.likeAnimation(false) {
                    likeCount = (likeCount.toInt() - 1).toString()
                    commentHeaderLikeCount.setLikeText(likeCount)
                }
                userLiked = false
                removeLike(view.id) {}
            } else {
                commentHeaderLikeBtn.likeAnimation(true) {
                    likeCount = (likeCount.toInt() + 1).toString()
                    commentHeaderLikeCount.setLikeText(likeCount)
                }
                userLiked = true
                pushLike(view.id) {}
            }
        }
    }

    private fun updateLike() {
        commentHeaderLikeBtn.likeAnimation(userLiked) {}
        commentHeaderLikeCount.setLikeText(likeCount)
    }


    override fun onAttach(view: CommentView) {

    }

    override fun onDettach() {
        //likeBtnListener
        //if (::imagePinchZoom.isInitialized)
        //   Zoomy.unregister(postImage)
    }
}