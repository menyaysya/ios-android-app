package com.wdt.uastrong.utils

import com.wdt.uastrong.database.AUTH
import com.wdt.uastrong.database.CHILD_STATE
import com.wdt.uastrong.database.NODE_USERS
import com.wdt.uastrong.database.REF_DATABASE_ROOT
import com.wdt.uastrong.database.UID
import com.wdt.uastrong.database.USER

enum class AppStates(val state: String) {
    ONLINE("в мережі"),
    OFFLINE("не в мережі"),
    TYPING("друкує");

    companion object {
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
                    .updateChildren(mapOf(CHILD_STATE to appStates.state))
                    .addOnSuccessListener { USER.state = appStates.state }
            }
        }
    }
}