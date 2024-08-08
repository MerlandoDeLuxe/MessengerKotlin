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
import java.util.concurrent.ThreadLocalRandom

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
    private val pathToOpenPhotos = "OpenPhotos"
    val collectionUserPhoto: ConcurrentHashMap<String, Uri> = ConcurrentHashMap()
    val userPhotoLD: MutableLiveData<ConcurrentHashMap<String, Uri>> = MutableLiveData()


    init {
        getAllUserPhotos() //При инициализации вью модели всегда запрашиваем новые фоточки из Storage
    }

    fun getAllUserPhotos() {
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(pathToOpenPhotos)
                .listAll()
                .addOnSuccessListener {
                    for (i in 0..it.items.count() - 1) {
                        //Пришли в storage по указанному пути, чтобы получить имя фотки и её полную ссылку для скачивания
                        val name = it.items.get(i).name
                        Log.d(TAG, "getAllUserPhotos: name = $name")

                        it.items.get(i).downloadUrl
                            .addOnSuccessListener {
                                //и добавляем её в коллекцию Map
                                collectionUserPhoto.put(name, it)

                                //а после успешного добавления всех фоток в лист, добавляем коллекци в Лайв Дату
                                userPhotoLD.value = collectionUserPhoto
                                Log.d(
                                    TAG,
                                    "addOnCompleteListener: collectionUserPhoto = ${collectionUserPhoto}"
                                )
                            }
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "getAllUserPhotos: Ошибка получения списка фотографий из Storage: ${it.message}")
                }
        }
    }

    fun checkSelectedUserPhotoForDelete(uri: Uri) {
        //Перед полноценным удалением фото, нужно удостовериться, что это именно она
        //Удаление происходит по ключу (имя объекта в Map и имя объекта в Storage)
        if (user != null) {
            referenceStorageUserPhoto
                .child(user.uid)
                .child(pathToOpenPhotos)
                .listAll()
                .addOnSuccessListener {
                    for ((key, value) in collectionUserPhoto) {
                        //Ищем файл в коллекции по Uri который у нас передается в метод и который есть в Map из Storage
                        if (value.equals(uri)) {
                            Log.d(
                                TAG,
                                "checkSelectedUserPhotoForDelete: Файл найден для удаления: $key}"
                            )
                            for (i in 0..it.items.count() - 1) {
                                //Файл выше был найден, идем в Storage и теперь ищем его по имени (ключу) из коллекции
                                if (it.items.get(i).name.equals(key)) {
                                    it.items.get(i).delete()
                                    Log.d(
                                        TAG,
                                        "checkSelectedUserPhotoForDelete: Удаление завершено"
                                    )
                                    collectionUserPhoto.remove(key)
                                    //Файл удалили не только из Storage, но и сразу из коллекции и обновили Лайв Дату для отображения
                                    userPhotoLD.value = collectionUserPhoto
                                }
                            }
                        }
                    }
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
                .child(pathToOpenPhotos)
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