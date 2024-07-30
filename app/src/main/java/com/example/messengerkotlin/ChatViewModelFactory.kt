package com.example.messengerkotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory (private val currentUserId: String, private val otherUserId: String): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatViewModel(currentUserId, otherUserId) as T
    }
}