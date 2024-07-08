package com.example.messengerkotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class ListOfUsersViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "ListOfUsersViewModel"
    private val auth = Firebase.auth
    var userEmail: MutableLiveData<String> = MutableLiveData<String>() //не используется
    var userLD: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()


    fun logout() {


//        if (auth.currentUser?.email != null) {
//            userEmail.value = auth.currentUser?.email
//            Log.d(TAG, "logout userEmail = $userEmail")
//        }
//
        auth.signOut()
        userLD.value = auth.currentUser

    }
}