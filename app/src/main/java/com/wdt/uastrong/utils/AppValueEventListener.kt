package com.wdt.uastrong.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import timber.log.Timber

class AppValueEventListener(val onSuccess: (DataSnapshot) -> Unit) : ValueEventListener {
    override fun onCancelled(error: DatabaseError) {
        Timber.e(error.toString())
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        onSuccess(snapshot)
    }
}