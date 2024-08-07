package com.example.messengerkotlin

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.component1
import com.google.firebase.storage.ktx.storage
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.log
import kotlin.random.Random

class MyProfileViewModel : ViewModel() {
    private val TAG = "MyProfileViewModel"
    private val ADMIN_EMAIL_ACCOUNT = "merlandodeluxe@gmail.com"
    private val newChildDatabase = "countUnreadMessages"
    private val USER_CHILD_STATUS = "online"

    private val database = FirebaseDatabase.getInstance()
    private val referenceUser = database.getReference("Users")

    private val auth = FirebaseAuth.getInstance();
    private val user = auth.currentUser

    val isUserAdminLD: MutableLiveData<Boolean> = MutableLiveData()

    private val storage = FirebaseStorage.getInstance()
    private val referenceStorageUserPhoto = storage.getReference("UserPhotoGallery")
    private val path = "OpenPhotos"
    val pathToPhotoLD: MutableLiveData<List<Uri>> = MutableLiveData()


    fun getAllUserPhotos() {
        val listOfLinks: MutableList<Uri> = mutableListOf()

        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(path)
                .listAll()
                .addOnSuccessListener {
                    Log.d(TAG, "getAllUserPhotos: it.items = ${it.items}")
                    Log.d(TAG, "getAllUserPhotos: it.prefix = ${it.prefixes}")
                    for (i in 0..it.items.count() - 1) {

                        it.items.get(i).downloadUrl
                            .addOnSuccessListener {
                                val temp = it
                                Log.d(TAG, "getAllUserPhotos: temp = $temp")
                                listOfLinks.add(temp)
                            }
                            .addOnCompleteListener {
                                pathToPhotoLD.value = listOfLinks
                                Log.d(
                                    TAG,
                                    "getAllUserPhotos: pathToPhotoLD = ${pathToPhotoLD.value}"
                                )
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "getAllUserPhotos: Ошибка: ${it.message}")
                }
        }
    }

    fun saveUserData(name: String, surname: String, age: Int, userInfo: String) {
        if (user != null) {
            referenceUser.child(user.uid).child("name").setValue(name)
            referenceUser.child(user.uid).child("surname").setValue(surname)
            referenceUser.child(user.uid).child("age").setValue(age)
            referenceUser.child(user.uid).child("userInfo").setValue(userInfo)
        }
    }

    fun randomizeUuid(): Long {
        return ThreadLocalRandom.current().nextLong(9_223_327_036_854_775_807)
    }

    fun changeUserPhoto(userPhoto: Uri) {
        val temp = randomizeUuid()
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(path)
                .child(temp.toString())
                .putFile(userPhoto)
                .addOnSuccessListener { Log.d(TAG, "changeUserPhoto: Фото добавлено") }
                .addOnFailureListener { Log.d(TAG, "changeUserPhoto: Ошибка: ${it.message}") }
        }
    }

    fun checkAdminUser() {
        if (user?.email.equals(ADMIN_EMAIL_ACCOUNT)) {
            isUserAdminLD.value = true
        } else {
            isUserAdminLD.value = false
        }
    }

    fun updateAllUsers() {
        Log.d(TAG, "updateAllUsers: Начало выполнения")
        //Функция для добавления всем пользователям в БД нового свойства. Перед этим его нужно обяъвить в классе
        if (user != null) {
            referenceUser.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
//                        val user = snap.getValue(User::class.java)!!
                        val hashMap = HashMap<String, Any>()
                        hashMap[newChildDatabase] =
                            0 //Сразу значение для вставки всем пользователям в это поле
                        snap.ref.updateChildren(hashMap)
                        Log.d(
                            TAG,
                            "onDataChange: выполнена вставка поля $newChildDatabase в БД для всех пользователей"
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    fun setUserOnline(isOnline: Boolean) {
        if (user != null) {
            referenceUser.child(user.uid).child(USER_CHILD_STATUS).setValue(isOnline)
        }
    }
}