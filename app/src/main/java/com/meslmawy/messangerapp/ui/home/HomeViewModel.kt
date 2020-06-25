package com.meslmawy.messangerapp.ui.home

import androidx.annotation.NonNull
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.meslmawy.messangerapp.models.Chat
import com.meslmawy.messangerapp.models.SingleLiveEvent
import com.meslmawy.messangerapp.models.State
import com.meslmawy.messangerapp.models.User
import java.util.HashMap

class HomeViewModel() : ViewModel() {


    private val saveLoginEvent = SingleLiveEvent<State>()
    fun state(): LiveData<State> = saveLoginEvent

    private val _titleName = MutableLiveData<String>()
    val titleName: LiveData<String>
        get() = _titleName

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl : LiveData<String>
        get() = _imageUrl

    private var firebaseAuth = FirebaseAuth.getInstance()
    var reference: DatabaseReference? = null

    private val _chatsNumber = MutableLiveData<Int>()
    val chatsNumber : LiveData<Int>
        get() = _chatsNumber

    init {
        reference = firebaseAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference("Users").child(
                it
            )
        }
        getToolBarData()
    }

    private fun getToolBarData(){
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    _titleName.value = user.userName
                }
                if (user != null) {
                    if (user.imageUrl.equals("default")) {
                        _imageUrl.value = "deafault"
                    } else {
                        _imageUrl.value = user.imageUrl
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })
    }

    fun status(status: String) {
        reference = firebaseAuth.currentUser?.uid?.let {
            FirebaseDatabase.getInstance().getReference("Users").child(
                it
            )
        }
        val hashMap = HashMap<String, Any>()
        hashMap["status"] = status
        reference?.updateChildren(hashMap)
    }
    fun getChats() {
        reference = FirebaseDatabase.getInstance().getReference("Chats")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                var unread = 0
                for (snapshot in dataSnapshot.children) {
                    val chat: Chat? = snapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        if (chat.receiver == firebaseAuth.currentUser?.uid && !chat.isseen!!) {
                            unread++
                        }
                    }
                }
                _chatsNumber.value = unread

            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
        })
    }
}

