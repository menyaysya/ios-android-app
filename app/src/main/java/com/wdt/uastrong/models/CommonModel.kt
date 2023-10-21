package com.wdt.uastrong.models

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",
    var email: String = "",
    var text: String = "",
    var type: String = "",
    var from: String = "",
    val to: String = "",
    var timeStamp: Any = "",

    val fileUrl: String = "empty",

    var lastMessage: String = "",
    var token: String = "",

    var choise: Boolean = false, //choise for create group

    //Feed
    var title: String = "",
    var description: String = "",
    var likeCount: String = "",
    var commentCount: Long = 0,
    var newsID: String = ""
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }
}