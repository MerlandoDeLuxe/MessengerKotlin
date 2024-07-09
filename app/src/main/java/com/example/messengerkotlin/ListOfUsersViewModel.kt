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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ListOfUsersViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "ListOfUsersViewModel"
    private val auth = Firebase.auth
    var userEmail: String? = auth.currentUser?.email
    var userLD: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()
    private val database = Firebase.database
    private val databaseReference = database.getReference("Users")
    val userListLD: MutableLiveData<List<User>> = MutableLiveData()
    private val listOfUsersFromDb: MutableList<User> = mutableListOf()

    fun getUsersFromDb() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = auth.currentUser
                if (currentUser != null)
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        if (user != null) {
                            if (currentUser.uid != user.id) {
                                Log.d(TAG, "onDataChange: listOfUsersFromDb = $listOfUsersFromDb")
                                listOfUsersFromDb.add(user)
                                Log.d(TAG, "onDataChange: listOfUsersFromDb = $listOfUsersFromDb")
                            }
                        }
                    }
                userListLD.value = listOfUsersFromDb
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

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