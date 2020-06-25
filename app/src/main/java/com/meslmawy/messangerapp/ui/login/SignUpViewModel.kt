package com.meslmawy.messangerapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.meslmawy.messangerapp.models.SingleLiveEvent
import com.meslmawy.messangerapp.models.State
import com.meslmawy.messangerapp.models.User
import kotlinx.coroutines.launch
import java.util.*

class SignUpViewModel() : ViewModel() {

    private val saveLoginEvent = SingleLiveEvent<State>()
    fun state(): LiveData<State> = saveLoginEvent
    private var firebaseAuth = FirebaseAuth.getInstance()
    var reference: DatabaseReference? = null
    private val toastMessage = MutableLiveData<String>()
    val showToast: LiveData<String>
        get() = toastMessage


    fun signUp(user: User) {
        saveLoginEvent.postValue(State(State.Status.LOADING))
        viewModelScope.launch {
            try {
                user.email?.let {
                    user.password?.let { it1 ->
                        firebaseAuth.createUserWithEmailAndPassword(it, it1)
                            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                                if (task.isSuccessful) {
                                    val firebaseUser: FirebaseUser = firebaseAuth.currentUser!!
                                    val userid = firebaseUser.uid
                                    reference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(userid)
                                    val hashMap = HashMap<String, String>()
                                    hashMap["id"] = userid
                                    hashMap["username"] = user.userName.toString()
                                    hashMap["imageURL"] = "default"
                                    hashMap["status"] = "offline"
                                    hashMap["search"] = user.search.toString()
                                    reference!!.setValue(hashMap)
                                        .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                                            if (task.isSuccessful) {
                                                saveLoginEvent.postValue(State(State.Status.SUCCESS))
                                            }
                                        })
                                }
                            })
                            .addOnFailureListener {
                                saveLoginEvent.postValue(State(State.Status.ERROR, error = it))
                            }
                    }
                }
            } catch (e: Exception) {
                saveLoginEvent.postValue(State(State.Status.ERROR, error = e))
            }
        }
    }

    fun showToast(string: String) {
        toastMessage.value = string
    }

}
