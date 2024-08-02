package com.example.messengerkotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG: String = "LoginViewModel"
    private var auth: FirebaseAuth = Firebase.auth

    val firebaseUserLD = MutableLiveData<FirebaseUser>()
    val authErrorMessageLD = MutableLiveData<String>()
    val isAuthSuccessLD = MutableLiveData(false)

    fun authStateListener() {
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                firebaseUserLD.value = it.currentUser
                Log.d(TAG, "authStateListener: firebaseUserLD = ${firebaseUserLD.value}")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firebaseUserLD.value = it.user
            }
            .addOnFailureListener {
                Log.d(TAG, "Ошибка при входе: ${it.message}")
                authErrorMessageLD.value = "Ошибка при входе: ${it.message}"
                Log.d(TAG, "firebaseUserLD: ${firebaseUserLD.value}")
            }
    }
}