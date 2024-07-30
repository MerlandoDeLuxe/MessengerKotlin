package com.example.messengerkotlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatViewModel(private val currentUserId: String, private val otherUserId: String) : ViewModel() {

    val messagesLD: MutableLiveData<List<Message>> = MutableLiveData()
    val otherUserLD: MutableLiveData<User> = MutableLiveData()
    val isMessageSentLD: MutableLiveData<Boolean> = MutableLiveData()
    val errorLD: MutableLiveData<String> = MutableLiveData()

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val referenceUsers = database.getReference("Users")
    private val referenceMessages = database.getReference("Messages")

    //Отслеживаем пользователя с которым ведется переписка:
    fun getOtherUser() {
        referenceUsers.child(otherUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                //Устанавливливаем этот объект в livedata
                otherUserLD.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getAllMessagesBetweenUsers() {
        //получаем переписку между двумя пользователями
        referenceMessages.child(currentUserId).child(otherUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val messageList: MutableList<Message> = mutableListOf()
                    for (snap in snapshot.children){
                        //получаем снапшот всех сообщений и добавляем их в коллекцию. let - проверка, что там не null
                        snap.getValue(Message::class.java)?.let { messageList.add(it) }
                    }
                    messagesLD.value = messageList
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun sendMessage(message: Message) {
        referenceMessages
            .child(message.senderId)
            .child(message.recieverId)
            .push()
            .setValue(message)
            .addOnSuccessListener {
                referenceMessages
                    .child(message.recieverId)
                    .child(message.senderId)
                    .push()
                    .setValue(message)
                    .addOnSuccessListener {
                        isMessageSentLD.value = true
                    }
                    .addOnFailureListener {
                        errorLD.value = it.message
                    }
            }
            .addOnFailureListener {
                errorLD.value = it.message
            }
    }

    fun setUserOnline(isOnline: Boolean){
        referenceUsers.child(currentUserId).child("online").setValue(isOnline)
    }
}