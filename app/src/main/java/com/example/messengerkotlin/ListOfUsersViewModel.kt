package com.example.messengerkotlin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ListOfUsersViewModel
    () : ViewModel() {
    private val TAG: String = "ListOfUsersViewModel"

    private val auth = Firebase.auth
    private val user = auth.currentUser
    var userEmail: String? = auth.currentUser?.email

    var userLD: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()
    val userListLD: MutableLiveData<List<User>> = MutableLiveData()

    private val database = Firebase.database
    private val referenceUsers = database.getReference("Users")
    private val USER_CHILD_STATUS = "online"
    private val USER_CHILD_PHOTO_URI = "userMainPhoto"
    val userMainPhotoLD: MutableLiveData<String> = MutableLiveData()

    init {
        referenceUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = auth.currentUser
                if (currentUser != null) {

                    val listOfUsersFromDb: MutableList<User> = mutableListOf()

                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        Log.d(TAG, "onDataChange: user = $user")
                        Log.d(TAG, "onDataChange: user.id = $user.id")
                        if (user != null) {

                            userMainPhotoLD.value = user.userMainPhoto

                            if (!currentUser.uid.equals(user.id)) {
                                listOfUsersFromDb.add(user)
                            }
                            if (currentUser.uid.equals(user.id)) {//Установка приоритета для дальнейшей сортировки
                                //чтобы текущий юзер всегда был первый в списке
                                user.priority = 1
                                listOfUsersFromDb.add(user)
                            }
                        } else {
                            return
                        }
                    }
                    listOfUsersFromDb.sortByDescending { it.priority } //Сортируем по приоритету
                    userListLD.value = listOfUsersFromDb
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setUserOnline(isOnline: Boolean) {
        if (user != null) {
            referenceUsers.child(user.uid).child(USER_CHILD_STATUS).setValue(isOnline)
        }
    }

    fun logout() {
//        if (auth.currentUser?.email != null) {
//            userEmail.value = auth.currentUser?.email
//            Log.d(TAG, "logout userEmail = $userEmail")
//        }
//
        setUserOnline(false)
        auth.signOut()
        userLD.value = auth.currentUser

    }
}