package com.wdt.uastrong.database
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.wdt.uastrong.models.UserModel

lateinit var AUTH: FirebaseAuth
lateinit var UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel


const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_EMAILS = "emails"
const val NODE_PHONES = "phones"
const val NODE_MESSAGES = "messages"
const val NODE_MESSAGE = "message"
const val FOLDER_PROFILE_IMG = "profile_image"
const val FOLDER_FILES = "messages_files"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_PHONES_CONTACTS = "phones_contacts"
const val CHILD_USERNAME = "username"
const val CHILD_EMAIL = "email"
const val CHILD_FULLNAME = "fullname"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TO = "to"
const val CHILD_TIMESTAMPT = "timeStamp"
const val CHILD_FILE_URL = "fileUrl"

const val NODE_MAIN_LIST = "main_list"

//Groups
const val NODE_GROUPS = "groups"
const val NODE_MEMBERS = "members"
const val FOLDER_GROUPS_AVATAR = "groups_avatar"
const val USER_CREATOR = "creator"
const val USER_ADMIN = "admin"
const val USER_MEMBER = "member"

//News
const val NODE_FEEDS = "feeds"
const val NODE_COMMENTS = "comments"
const val CHILD_COMMENT_COUNT = "commentCount"
const val CHILD_LIKE_COUNT = "likeCount"
const val FOLDER_POST_FILES = "post_files"

const val NODE_LIKES = "likes"
const val CHILD_NEWS_ID = "newsID"
const val CHILD_DESCRIPTION = "description"

///////FIRESTORE