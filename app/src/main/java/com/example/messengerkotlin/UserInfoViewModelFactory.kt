package com.example.messengerkotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserInfoViewModelFactory (private val otherUserId: String): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserInfoViewModel(otherUserId) as T
    }
}