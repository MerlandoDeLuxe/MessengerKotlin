package com.example.messengerkotlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserInfoViewModel (private val otherUserId: String): ViewModel() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val referenceUser = database.getReference("Users")
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    val otherUserLD: MutableLiveData<User> = MutableLiveData()

    //Получаем пользователя с кем общаемся, записываем его в лайв дату
    fun getOtherUserData(){
        referenceUser.child(otherUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                otherUserLD.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //текущему пользователю ставим статус в сети или нет
    fun setUserOnline(isOnline: Boolean) {
        if (user != null) {
            referenceUser.child(user.uid).child("online").setValue(isOnline)
        }
    }
}