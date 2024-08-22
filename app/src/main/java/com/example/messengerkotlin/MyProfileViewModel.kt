package com.example.messengerkotlin

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.ConcurrentHashMap

class MyProfileViewModel : ViewModel() {
    private val TAG = "MyProfileViewModel"
    private val ADMIN_EMAIL_ACCOUNT = "merlandodeluxe@gmail.com"
    private val newChildDatabase = "typing"
    private val USER_CHILD_STATUS = "online"
    private val USER_CHILD_PHOTO_URI = "userMainPhoto"

    private val database = FirebaseDatabase.getInstance()
    private val referenceUser = database.getReference("Users")
    private val referenceMessages = database.getReference("Messages")


    private val auth = FirebaseAuth.getInstance();
    private val user = auth.currentUser

    val isUserAdminLD: MutableLiveData<Boolean> = MutableLiveData()

    private val storage = FirebaseStorage.getInstance()
    private val referenceStorageUserPhoto = storage.getReference("UserPhotoGallery")
    private val pathToOpenPhotos = "OpenPhotos"
    private val collectionUserPhoto: ConcurrentHashMap<String, Uri> = ConcurrentHashMap()
    val collectionUserPhotoLD: MutableLiveData<ConcurrentHashMap<String, Uri>> = MutableLiveData()
    private val collectionUserMainPhoto: ConcurrentHashMap<String, Uri> = ConcurrentHashMap()
    val collectionUserMainPhotoLD: MutableLiveData<ConcurrentHashMap<String, Uri>> =
        MutableLiveData()
    val isPhotosStillLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getAllUserPhotos() //При инициализации вью модели всегда запрашиваем новые фоточки из Storage
    }

    fun setUserMainPhoto(uri: Uri) {
        Log.d(TAG, "setUserMainPhoto: ")
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(pathToOpenPhotos)
                .listAll()
                .addOnSuccessListener {
                    for ((key, value) in collectionUserPhoto) {
                        isPhotosStillLoading.value = true
                        //Ищем файл в коллекции по Uri который у нас передается в метод и который есть в Map из Storage
                        if (value.equals(uri)) {
                            collectionUserMainPhoto.clear()
                            collectionUserMainPhoto.put(key, value)
                            collectionUserMainPhotoLD.value = collectionUserMainPhoto
                            Log.d(
                                TAG,
                                "checkSelectedUserPhotoForDelete: Файл найден для установки Main Photo: $key}"
                            )
                            if (it.items.count() == 0) {
                                //Если фотографий в storage не осталось, то передаем в Лайв дату пустой лист, чтобы скрыть recycleView
                                Log.d(
                                    TAG,
                                    "Фотографий больше нет добавлять на Main Photo нечего, отправляем в Лайв Дату пустую строку"
                                )
                                collectionUserPhoto.put("", "".toUri())
                                collectionUserPhotoLD.value = collectionUserPhoto
                            }
                        }
                    }
                }
                .addOnCompleteListener {
                    isPhotosStillLoading.value = false
                }
                .addOnFailureListener { }
        }

        if (user != null) {
            referenceUser
                .child(user.uid)
                .child(USER_CHILD_PHOTO_URI)
                .setValue(uri.toString())
        }
    }

    fun getAllUserPhotos() {
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(pathToOpenPhotos)
                .listAll()
                .addOnSuccessListener {
                    isPhotosStillLoading.value = true //Для progressBar
                    Log.d(
                        TAG,
                        "getAllUserPhotos: isPhotosStillLoading = ${isPhotosStillLoading.value}"
                    )
                    for (i in 0..it.items.count() - 1) {
                        //Пришли в storage по указанному пути, чтобы получить имя фотки и её полную ссылку для скачивания
                        val name = it.items.get(i).name
                        Log.d(TAG, "getAllUserPhotos: name = $name")

                        it.items.get(i).downloadUrl
                            .addOnSuccessListener {
                                //и добавляем её в коллекцию Map
                                collectionUserPhoto.put(name, it)
                                //а после успешного добавления всех фоток в лист, добавляем коллекци в Лайв Дату
                                collectionUserPhotoLD.value = collectionUserPhoto
                                Log.d(
                                    TAG,
                                    "addOnCompleteListener: collectionUserPhoto = ${collectionUserPhoto}"
                                )
                                isPhotosStillLoading.value = false  //Для progressBar
                                Log.d(
                                    TAG,
                                    "getAllUserPhotos: isPhotosStillLoading = ${isPhotosStillLoading.value}"
                                )
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d(
                        TAG,
                        "getAllUserPhotos: Ошибка получения списка фотографий из Storage: ${it.message}"
                    )
                }
        }
    }

    fun deleteSelectedPhoto(uri: Uri) {
        //Перед полноценным удалением фото, нужно удостовериться, что это именно она
        //Удаление происходит по ключу (имя объекта в Map и имя объекта в Storage)
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(pathToOpenPhotos)
                .listAll()
                .addOnSuccessListener {
                    isPhotosStillLoading.value = true  //Для progressBar
                    for (i in 0..it.items.count() - 1) {
                        for ((key, value) in collectionUserPhoto) {
                            //Ищем файл в коллекции по Uri который у нас передается в метод и который есть в Map из Storage
                            if (value.equals(uri)) {
                                Log.d(
                                    TAG,
                                    "checkSelectedUserPhotoForDelete: Файл найден для удаления: $key}"
                                )
                                it.items.get(i).delete()
                                collectionUserPhoto.remove(key)
                                collectionUserPhotoLD.value = collectionUserPhoto
                                collectionUserMainPhoto.remove(key)
                                collectionUserMainPhotoLD.value = collectionUserMainPhoto
                                Log.d(
                                    TAG,
                                    "deleteSelectedPhoto: collectionUserMainPhoto = $collectionUserMainPhoto"
                                )
                            }
                        }
                        isPhotosStillLoading.value = false  //Для progressBar
                    }

                    if (it.items.count() == 0) {
                        //Если фотографий в storage не осталось, то передаем в Лайв дату пустой лист, чтобы скрыть recycleView
                        Log.d(
                            TAG,
                            "Фотографий больше нет, отправляем в Лайв Дату пустую строку"
                        )
                        collectionUserPhoto.put("", "".toUri())
                        collectionUserPhotoLD.value = collectionUserPhoto
                    }
                }
        }
        if (user != null) {
            referenceUser
                .child(user.uid)
                .child(USER_CHILD_PHOTO_URI)
                .setValue("")
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
        return (Math.random() * 9_223_372_036_854_775).toLong()
    }

    fun addNewUserPhoto(userPhoto: Uri) {
        // val temp = randomizeUuid()
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(pathToOpenPhotos)
                .child(randomizeUuid().toString())
                .putFile(userPhoto)
                .addOnSuccessListener {
                    isPhotosStillLoading.value = true  //Для progressBar
                    Log.d(TAG, "addNewUserPhoto: Фото добавлено")
                    //Log.d(TAG, "addNewUserPhoto: temp = $temp")
                    getAllUserPhotos()
                }
                .addOnFailureListener { Log.d(TAG, "addNewUserPhoto: Ошибка: ${it.message}") }
                .addOnCompleteListener({
                    isPhotosStillLoading.value = false  //Для progressBar
                })
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
//        //Функция для добавления всем пользователям в БД нового свойства. Перед этим его нужно объявить в классе
//        if (user != null) {
//            referenceUser.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (snap in snapshot.children) {
////                        val user = snap.getValue(User::class.java)!!
//                        val hashMap = HashMap<String, Any>()
//                        hashMap[newChildDatabase] =
//                            false //Сразу значение для вставки всем пользователям в это поле
//                        snap.ref.updateChildren(hashMap)
//                        Log.d(
//                            TAG,
//                            "onDataChange: выполнена вставка поля $newChildDatabase в БД для всех пользователей"
//                        )
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })
//        }

    }

    fun setUserOnline(isOnline: Boolean) {
        if (user != null) {
            referenceUser.child(user.uid).child(USER_CHILD_STATUS).setValue(isOnline)
        }
    }
}