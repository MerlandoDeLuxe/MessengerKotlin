package com.example.messengerkotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG: String = "ForgotPasswordViewModel"
    private val auth = Firebase.auth
    var isSendSuccessLD: MutableLiveData<String> = MutableLiveData()
    var isSendedLD: MutableLiveData<Boolean> = MutableLiveData()

    fun passwordRecovery(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Log.d(TAG, "passwordRecovery: ссылка на почту $email отправлена")
                isSendSuccessLD.value = "Ссылка на восстановление отправлена на указанную почту"
                isSendedLD.value = true
                isSendedLD.value = null
                isSendSuccessLD.value = null
                //Если не присвоить null, то при повторном открытии
                // формы сброса пароля, снова вызовется метод observe,
                // т.к. вью модель еще жива и хранит значение и, значит, выведется тост
                Log.d(TAG, "passwordRecovery: isSendedLD = ${isSendedLD.value}")
                Log.d(TAG, "passwordRecovery: isSendSuccessLD = ${isSendSuccessLD.value}")
            }
            .addOnFailureListener {
                Log.d(TAG, "passwordRecovery: Ошибка при отправке на почту ${it.message}")
                isSendSuccessLD.value = "Ошибка при отправке на почту: ${it.message}"
                isSendedLD.value = null
                isSendSuccessLD.value = null
                Log.d(TAG, "passwordRecovery: isSendedLD = ${isSendedLD.value}")
                Log.d(TAG, "passwordRecovery: isSendSuccessLD = ${isSendSuccessLD.value}")
            }
    }
}