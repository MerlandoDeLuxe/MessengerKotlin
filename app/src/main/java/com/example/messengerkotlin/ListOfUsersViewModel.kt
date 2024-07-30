package com.example.messengerkotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ListOfUsersViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "ListOfUsersViewModel"
    private val auth = Firebase.auth
    private val user = auth.currentUser
    var userEmail: String? = auth.currentUser?.email
    var userLD: MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()
    private val database = Firebase.database
    private val referenceUsers = database.getReference("Users")
    val userListLD: MutableLiveData<List<User>> = MutableLiveData()
    private val listOfUsersFromDb: MutableList<User> = mutableListOf()

    fun getUsersFromDb() {
        referenceUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = auth.currentUser
                if (currentUser != null)
                    for (snap in snapshot.children) {
                        val user = snap.getValue(User::class.java)
                        if (user != null) {
                            if (!currentUser.uid.equals(user.id)) {
                                Log.d(TAG, "onDataChange: listOfUsersFromDb = $listOfUsersFromDb")
                                listOfUsersFromDb.add(user)
                                Log.d(TAG, "onDataChange: listOfUsersFromDb = $listOfUsersFromDb")
                            }
                            if (currentUser.uid.equals(user.id)) {//Установка приоритета для дальнейшей сортировки
                                //чтобы текущий юзер всегда был первый в списке
                                user.priority = 1
                                listOfUsersFromDb.add(user)
                            }
                        }
                    }
                listOfUsersFromDb.sortByDescending { it.priority } //Сортируем по приоритету
                userListLD.value = listOfUsersFromDb
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setUserOnline(isOnline: Boolean) {
        if (user != null) {
            referenceUsers.child(user.uid).child("online").setValue(isOnline)
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