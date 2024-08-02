package com.example.messengerkotlin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyProfileViewModel : ViewModel() {
    private val TAG = "MyProfileViewModel"
    private val ADMIN_EMAIL_ACCOUNT = "merlandodeluxe@gmail.com"
    private val newFieldToDatabase = "countUnreadMessages"
    private val database = FirebaseDatabase.getInstance()
    private val referenceUser = database.getReference("Users")
    private val auth = FirebaseAuth.getInstance();
    private val user = auth.currentUser
    val surname: MutableLiveData<String> = MutableLiveData()
    val isUserAdmin: MutableLiveData<Boolean> = MutableLiveData()

    fun saveUserData(name: String, surname: String, age: Int, userInfo: String) {
        if (user != null) {
            referenceUser.child(user.uid).child("name").setValue(name)
            referenceUser.child(user.uid).child("surname").setValue(surname)
            referenceUser.child(user.uid).child("age").setValue(age)
            referenceUser.child(user.uid).child("userInfo").setValue(userInfo)
        }
    }

    fun checkAdminUser(){
        if (user?.email.equals(ADMIN_EMAIL_ACCOUNT)) {
            isUserAdmin.value = true
        }
        else{
            isUserAdmin.value = false
        }
    }

    fun updateAllUsers(){
        Log.d(TAG, "updateAllUsers: Начало выполнения")
        //Функция для добавления всем пользователям в БД нового свойства. Перед этим его нужно обяъвить в классе
        if (user != null){
            referenceUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children){
//                        val user = snap.getValue(User::class.java)!!
                        val hashMap = HashMap<String, Any>()
                        hashMap[newFieldToDatabase] = 0 //Сразу значение для вставки всем пользователям в это поле
                        snap.ref.updateChildren(hashMap)
                        Log.d(TAG, "onDataChange: выполнена вставка поля $newFieldToDatabase в БД для всех пользователей")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
    fun getSurname(){

    }
}