package com.wdt.uastrong.database

import android.net.Uri
import android.text.TextUtils

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.wdt.uastrong.utils.APP_ACTIVITY
import com.wdt.uastrong.R
import com.wdt.uastrong.models.CommonModel
import com.wdt.uastrong.models.UserModel
import com.wdt.uastrong.utils.AppValueEventListener
import com.wdt.uastrong.utils.TYPE_COMMENT
import com.wdt.uastrong.utils.TYPE_FEED_DEFAULT
import com.wdt.uastrong.utils.TYPE_GROUP
import com.wdt.uastrong.utils.TYPE_MESSAGE_TEXT
import com.wdt.uastrong.utils.hideKeyboard
import com.wdt.uastrong.utils.showToast

import org.json.JSONObject
import timber.log.Timber
import java.io.File


fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID).child(id)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

fun pushLike(
    newsId: String,
    function: () -> Unit
) {
    val likePath = REF_DATABASE_ROOT.child(NODE_FEEDS).child(newsId).child(NODE_LIKES).child(UID)
    likePath.push().child(UID).setValue(true)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }

}

fun commentPushLike(
    newsId: String,
    function: () -> Unit
) {
    val likePath = REF_DATABASE_ROOT.child(NODE_COMMENTS).child(newsId).child(NODE_LIKES).child(UID)
    likePath.push().child(UID).setValue(true)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }

}

fun commentRemoveLike(
    newsId: String,
    function: () -> Unit
) {
    val likePath = REF_DATABASE_ROOT.child(NODE_COMMENTS).child(newsId).child(NODE_LIKES).child(UID)
    likePath.removeValue()
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }

}


fun removeComment(
    id: String,
    newsId: String,
    function: () -> Unit
) {
    val postPath = REF_DATABASE_ROOT.child(NODE_COMMENTS).child(newsId).child(id)
    postPath.removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_FEEDS)
                .child(newsId).child(CHILD_COMMENT_COUNT)
                .setValue(ServerValue.increment(-1))
                .addOnSuccessListener { function() }
                .addOnFailureListener {
                    showToast(APP_ACTIVITY.getString(R.string.Error))
                    APP_ACTIVITY.onStopLoading()
                }
        }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

fun removeFile(
    id: String,
    folder: String,
    function: () -> Unit
) {
    REF_STORAGE_ROOT.child(folder).child(id)
        .delete()
        .addOnSuccessListener { function() }
}

fun removeNewsComments(
    newsId: String,
    function: () -> Unit
) {
    REF_DATABASE_ROOT.child(NODE_COMMENTS).child(newsId)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

fun removePost(
    newsId: String,
    function: () -> Unit
) {
    val newsRef = REF_DATABASE_ROOT.child(NODE_FEEDS).child(newsId)

    newsRef.removeValue()
        .addOnSuccessListener {
            removeNewsComments(newsId) {
                newsRef.child(CHILD_FILE_URL).get()
                    .addOnSuccessListener { snap ->
                        if (snap.value != "empty")
                            removeFile(newsId, FOLDER_POST_FILES) {
                                function()
                            }
                    }
            }
        }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}


fun removeLike(
    newsId: String,
    function: () -> Unit
) {
    REF_DATABASE_ROOT.child(NODE_FEEDS).child(newsId).child(NODE_LIKES).child(UID)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

fun getCommentsLikesData(
    id: String,
    function: (likeCount: String, userLiked: Boolean) -> Unit
) {

    val likesRef = REF_DATABASE_ROOT.child(NODE_COMMENTS).child(id).child(NODE_LIKES)
    var likeCount: Long
    var userLiked = false

    likesRef.addListenerForSingleValueEvent(AppValueEventListener {
        likeCount = it.childrenCount
        if (it.hasChild(UID))
            userLiked = true

        function(likeCount.toString(), userLiked)
    })
}

fun getNewsLikesData(
    id: String,
    function: (likeCount: String, userLiked: Boolean) -> Unit
) {

    val likesRef = REF_DATABASE_ROOT.child(NODE_FEEDS).child(id).child(NODE_LIKES)
    var likeCount: Long
    var userLiked = false

    likesRef.addListenerForSingleValueEvent(AppValueEventListener {
        likeCount = it.childrenCount
        if (it.hasChild(UID))
            userLiked = true

        function(likeCount.toString(), userLiked)
    })
}

fun createNewsInDatabase(
    description: String,
    photo: Uri,
    function: () -> Unit
) {
    val keyPost = REF_DATABASE_ROOT.child(NODE_FEEDS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_FEEDS).child(keyPost)
    val imagePath = REF_STORAGE_ROOT.child(FOLDER_POST_FILES).child(keyPost)

    if (photo != Uri.EMPTY) {
        putFileToStorage(photo, imagePath) {
            getUrlFromStorage(imagePath) { fileUrl ->

                val mapPost = hashMapOf<String, Any>()
                mapPost[CHILD_FROM] = UID
                mapPost[CHILD_TYPE] = TYPE_FEED_DEFAULT
                mapPost[CHILD_DESCRIPTION] = description
                mapPost[CHILD_ID] = keyPost
                mapPost[CHILD_FILE_URL] = fileUrl
                mapPost[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP

                path
                    .updateChildren(mapPost)
                    .addOnSuccessListener { function() }
                    .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
            }
        }
    } else {
        val mapPost = hashMapOf<String, Any>()
        mapPost[CHILD_FROM] = UID
        mapPost[CHILD_TYPE] = TYPE_FEED_DEFAULT
        mapPost[CHILD_DESCRIPTION] = description
        mapPost[CHILD_ID] = keyPost
        mapPost[CHILD_FILE_URL] = "empty"
        mapPost[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP


        path
            .updateChildren(mapPost)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
    }
}

fun updateFullname(newName: String) {

    APP_ACTIVITY.onStartLoading()
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .child(CHILD_FULLNAME)
        .setValue(newName)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.data_saved))
            USER.fullname = newName

            APP_ACTIVITY.supportFragmentManager.popBackStack()
            hideKeyboard()
            APP_ACTIVITY.onStopLoading()
        }
        .addOnFailureListener {
            Timber.e(it.toString())
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }

}

fun updateNickname(newNickname: String) {
    APP_ACTIVITY.onStartLoading()

    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .child(CHILD_USERNAME).setValue(newNickname)
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_USERNAMES).child(newNickname)
                .setValue(UID)
                .addOnSuccessListener {
                    showToast(APP_ACTIVITY.getString(R.string.data_saved))
                    deleteOldUserName(newNickname)
                }
                .addOnFailureListener {
                    Timber.e(it.toString())
                    showToast(APP_ACTIVITY.getString(R.string.Error))
                    APP_ACTIVITY.onStopLoading()
                }
        }
        .addOnFailureListener {
            Timber.e(it.toString())
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }

}


fun deleteOldUserName(newNickname: String) {
    APP_ACTIVITY.onStartLoading()

    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username)
        .removeValue()
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.data_saved))
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            hideKeyboard()
            USER.username = newNickname
        }
        .addOnFailureListener {
            Timber.e(it.toString())
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

fun createGroupInDatabase(
    nameGroup: String,
    mAvatarUri: Uri,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val keyGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val avatarPath = REF_STORAGE_ROOT.child(FOLDER_GROUPS_AVATAR).child(keyGroup)

    putFileToStorage(mAvatarUri, avatarPath) {
        getUrlFromStorage(avatarPath) {
            val mapData = hashMapOf<String, Any>()
            mapData[CHILD_ID] = keyGroup
            mapData[CHILD_FULLNAME] = nameGroup
            mapData[CHILD_PHOTO_URL] = it
            val mapMembers = hashMapOf<String, Any>()
            listContacts.forEach { model ->
                if (model.id != UID)
                    mapMembers[model.id] = USER_MEMBER
            }
            mapMembers[UID] = USER_CREATOR

            mapData[NODE_MEMBERS] = mapMembers

            path.updateChildren(mapData)
                .addOnSuccessListener {
                    addGroupToMainList(mapData, listContacts) {
                        APP_ACTIVITY.onStopLoading()
                        function()
                    }
                }
                .addOnFailureListener {
                    showToast(APP_ACTIVITY.getString(R.string.Error))
                    APP_ACTIVITY.onStopLoading()
                }
        }
    }
}

fun addGroupToMainList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val path = REF_DATABASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()
    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}


fun saveToMainList(id: String, type: String) {

    val refUser = "$NODE_MAIN_LIST/$UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type
    mapUser[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP

    mapReceived[CHILD_ID] = UID
    mapReceived[CHILD_TYPE] = type
    mapReceived[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener {
            showToast(it.message.toString())
            APP_ACTIVITY.onStopLoading()
            Timber.e(it.toString())
        }
}


fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        .removeValue()
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(UID)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener {
                    showToast(APP_ACTIVITY.getString(R.string.Error))
                    APP_ACTIVITY.onStopLoading()
                }
        }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}


inline fun getUrlFromStorage(
    path: StorageReference,
    crossinline function: (url: String) -> Unit
) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

inline fun putUrlToFirebase(url: String, crossinline function: () -> Unit) {
    APP_ACTIVITY.onStartLoading()
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .updateChildren(mapOf(CHILD_PHOTO_URL to url))
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            Timber.e(it.toString())
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }

}

inline fun putFileToStorage(
    uri: Uri,
    path: StorageReference,
    crossinline function: () -> Unit
) {

    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener {
            showToast(APP_ACTIVITY.getString(R.string.Error))
            APP_ACTIVITY.onStopLoading()
        }
}

inline fun initUser(crossinline function: () -> Unit) {
    if (AUTH.currentUser != null) {
        val userRef = REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        userRef.keepSynced(true)
        userRef
            .get()
            .addOnSuccessListener {
                USER = it.getValue(UserModel::class.java) ?: UserModel()
                if (USER.username.isEmpty()) {
                    USER.username = UID
                }
                function()
            }
    } else {
        function()
    }
}


fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()


fun sendMessage(message: String, contact: CommonModel, function: () -> Unit) {
    val recvID = contact.id
    val refDialogUser = "$NODE_MESSAGES/$UID/$recvID"
    val refDialogReceivinUser = "$NODE_MESSAGES/$recvID/$UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key
    val mapMessage = hashMapOf<String, Any>()
    val timeStamp = ServerValue.TIMESTAMP
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TO] = recvID
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_TEXT
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMPT] = timeStamp

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivinUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
}

fun removeMessage(
    id: String,
    toID: String,
    function: () -> Unit
) {
    val refMessageUser = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(toID).child(id)
    val refMessageDest = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(toID).child(UID).child(id)

    refMessageUser.removeValue()
        .addOnSuccessListener {
            refMessageDest.removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
        }
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }


}

fun sendComment(comment: String, newsID: String, function: () -> Unit) {
    val refNews = REF_DATABASE_ROOT.child(NODE_FEEDS)
        .child(newsID).child(CHILD_COMMENT_COUNT)


    val refComment = "$NODE_COMMENTS/$newsID"
    val commentKey = REF_DATABASE_ROOT.child(refComment).push().key

    val mapComment = hashMapOf<String, Any>()
    mapComment[CHILD_FROM] = UID
    mapComment[CHILD_TYPE] = TYPE_COMMENT
    mapComment[CHILD_NEWS_ID] = newsID
    mapComment[CHILD_DESCRIPTION] = comment
    mapComment[CHILD_ID] = commentKey.toString()
    mapComment[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP

    val mapSendComment = hashMapOf<String, Any>()
    mapSendComment["$refComment/$commentKey"] = mapComment

    REF_DATABASE_ROOT.updateChildren(mapSendComment)
        .addOnSuccessListener {
            refNews.setValue(ServerValue.increment(1))
                .addOnSuccessListener {
                    function()
                }

        }
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
}

fun sendMessageToGroup(message: String, groupID: String, function: () -> Unit) {
    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TO] = groupID
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_TEXT
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP


    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String
) {
    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserID"
    val refDialogReceivinUser = "$NODE_MESSAGES/$receivingUserID/$UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TO] = receivingUserID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivinUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT.updateChildren(mapDialog)
}

fun sendMessageAsFileToGroup(
    groupID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String, function: () -> Unit
) {
    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"


    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TO] = groupID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMPT] = ServerValue.TIMESTAMP
    mapMessage[CHILD_TEXT] = filename


    REF_DATABASE_ROOT.child(refMessages).child(messageKey)
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }

}

fun uploadFileToStorageFromGroup(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeMessage: String,
    filename: String = "",
    function: () -> Unit
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)
    APP_ACTIVITY.onStartLoading()
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFileToGroup(
                receivedID,
                it,
                messageKey,
                typeMessage,
                filename
            ) {
                function()
            }
        }
    }
}

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeMessage: String,
    filename: String = ""
) {

    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)

    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(
                receivedID,
                it,
                messageKey,
                typeMessage,
                filename
            )
            APP_ACTIVITY.onStopLoading()
        }
    }
}

fun getMessageKey(id: String, function: (key: String) -> Unit) {
    function(
        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
            .push().key.toString()
    )

}


fun initFirebase() {
    try {
        Firebase.database.setPersistenceEnabled(true)
    } catch (e: Exception) {
        Timber.e("LOCAL FIREBASE CACHE PROBLEM -->> $e")
    }


    AUTH = FirebaseAuth.getInstance()

    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.Error)) }
}

