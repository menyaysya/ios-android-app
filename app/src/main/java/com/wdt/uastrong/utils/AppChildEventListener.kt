package com.wdt.uastrong.utils


import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class AppChildEventListener(val onSuccess: (DataSnapshot,removed:Boolean) -> Unit) : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        onSuccess(snapshot,false)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        //TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        onSuccess(snapshot,true)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        //TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        //TODO("Not yet implemented")
    }

}