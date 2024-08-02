package com.example.messengerkotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "RegistrationViewModel"

    val userLD = MutableLiveData<FirebaseUser>()
    val wrongAuthTextLD = MutableLiveData<String>()

    private val database = FirebaseDatabase.getInstance()
    private val referenceUser = database.getReference("Users")
    private val auth = FirebaseAuth.getInstance()

    fun createUser(
        email: String,
        password: String,
        name: String,
        surname: String,
        age: Int
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, "createUser: Успешное создание пользователя ${it.user}")
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {

                    val user = User(
                        firebaseUser.uid,
                        name,
                        surname,
                        age,
                        false,
                        firebaseUser.email.toString()
                    )
                    referenceUser.child(user.id).setValue(user)
                    //После успешного создания пользователя, если не выйти, то откроется не повторный ввод логина пароля,
                    // а сразу список контактов
                    userLD.value = it.user
                    auth.signOut()
                }
            }
            .addOnFailureListener {
                wrongAuthTextLD.value = it.message
                Log.d(TAG, "createUser: Аутентификация завершилась с ошибкой: ${it.message}")
                Log.d(TAG, "createUser: wrongAuthTextLD = ${wrongAuthTextLD.value}")
            }
    }
}