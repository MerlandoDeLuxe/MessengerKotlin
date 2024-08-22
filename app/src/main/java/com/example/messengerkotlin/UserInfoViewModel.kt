package com.example.messengerkotlin

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.ConcurrentHashMap

class UserInfoViewModel(private val otherUserId: String) : ViewModel() {

    private val TAG = "UserInfoViewModel"
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val referenceUser = database.getReference("Users")
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser


    val otherUserLD: MutableLiveData<User> = MutableLiveData()
    val currentUserLD: MutableLiveData<User> = MutableLiveData()

    private val storage = FirebaseStorage.getInstance()
    private val referenceStorageUserPhoto = storage.getReference("UserPhotoGallery")
    private val pathToOpenPhotos = "OpenPhotos"
    private val collectionUserPhoto: ConcurrentHashMap<String, Uri> = ConcurrentHashMap()
    val collectionUserPhotoLD: MutableLiveData<ConcurrentHashMap<String, Uri>> = MutableLiveData()
    val isPhotosStillLoading: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getAllUserPhotos() //При инициализации вью модели всегда запрашиваем новые фоточки из Storage
    }


    fun getAllUserPhotos() {
        val uidSelectedUser: String
        if (user != null) {
            if (user.uid != otherUserId) { //Эта проверка нужна для того, чтобы не отображать в чужих инфо свои фотки. Если текущий пользователь равен
                //переданному в конструкторе другому - значит мы нажали на самого себя и тогда свои фото можно отобразить
                //но если там другой, то запрашиваем ЕГО фотографии
                Log.d(TAG, "getAllUserPhotos: user.uid = ${user.uid}")
                Log.d(TAG, "getAllUserPhotos: otherUserId = ${otherUserId}")
                uidSelectedUser = otherUserId
            } else {
                Log.d(TAG, "getAllUserPhotos: user.uid = ${user.uid}")
                Log.d(TAG, "getAllUserPhotos: otherUserId = ${otherUserId}")
                uidSelectedUser = user.uid
            }
            referenceStorageUserPhoto
                .child(uidSelectedUser)
                .child(pathToOpenPhotos)
                .listAll()
                .addOnSuccessListener {
                    isPhotosStillLoading.value = true //Для progressBar
                    Log.d(TAG, "getAllUserPhotos: isPhotosStillLoading = ${isPhotosStillLoading.value}")
                    collectionUserPhoto.clear()
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
                                isPhotosStillLoading.value = false //Для progressBar
                                Log.d(TAG, "getAllUserPhotos: isPhotosStillLoading = ${isPhotosStillLoading.value}")
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

    //Получаем пользователя с кем общаемся, записываем его в лайв дату
    fun getOtherUserData() {
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

    fun getCurrentUser() {
        if (user != null) {
            referenceUser.child(user.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    currentUserLD.value = user
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    //текущему пользователю ставим статус в сети или нет
    fun setUserOnline(isOnline: Boolean) {
        if (user != null) {
            referenceUser.child(user.uid).child("online").setValue(isOnline)
        }
    }
}