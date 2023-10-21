package com.wdt.uastrong.models

data class UserModel(
    val id: String = "",
    var username: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var email: String = "",
    var photoUrl: String = "empty"
)