package com.example.messengerkotlin

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatViewModel(private val currentUserId: String, private val otherUserId: String) :
    ViewModel() {
    private val TAG = "ChatViewModel"
    val messagesLD: MutableLiveData<List<Message>> = MutableLiveData()
    val otherUserLD: MutableLiveData<User> = MutableLiveData()
    val isMessageSentLD: MutableLiveData<Boolean> = MutableLiveData()
    val errorLD: MutableLiveData<String> = MutableLiveData()
    val isUserTyping: MutableLiveData<Boolean> = MutableLiveData()
    private var isTargetUserOnline = false

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val referenceUsers = database.getReference("Users")
    private val referenceMessages = database.getReference("Messages")
    private val pathListOfMessages = "ListOfMessages"
    private val pathTyping = "typing"
    private val pathOnlineWithTargetUser = "onlineWithTargetUser"
    private val countUnreadMessages = 0


    //Отслеживаем пользователя с которым ведется переписка:
    fun getOtherUser() {
        //Отслеживаем данные пользователя чтобы проставить ему статус онлайн или нет
        referenceUsers
            .child(otherUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    //Устанавливливаем этот объект в livedata
                    otherUserLD.value = user
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        //Отслеживаем данные в разделе Messages в Firebase, чтобы понять печатает он или нет
        referenceMessages
            .child(otherUserId) //Статус печати с текущим пользователем хранится в разделе того пользователя
            .child(currentUserId)
            .child(pathTyping)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {//Проверка что ветка с этим полем вообще существует, чтобы не ловили NullPointerException
                        isUserTyping.value = snapshot.getValue() as Boolean?
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun setOnlineWithTargetUser(status: Boolean) {
        referenceMessages
            .child(otherUserId)
            .child(currentUserId)
            .child(pathOnlineWithTargetUser)
            .setValue(status)
        isTargetUserOnline = status
        Log.d(TAG, "setOnlineWithTargetUser: isTargetUserOnline = $isTargetUserOnline")

    }

    fun getAllMessagesBetweenUsers() {
        //получаем переписку между двумя пользователями

        referenceMessages.child(currentUserId).child(otherUserId)
            .child(pathListOfMessages)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList: MutableList<Message> = mutableListOf()
                    val updateMessageMap: MutableMap<String, String> = mutableMapOf()
                    updateMessageMap.put("read", isTargetUserOnline.toString())
                    for (snap in snapshot.children) {
                        //получаем снапшот всех сообщений и добавляем их в коллекцию. let - проверка, что там не null
                        if (snap.exists()) {
                            snap.getValue(Message::class.java)
                                ?.let {
                                    messageList.add(it)
                                }
                        }
                    }
                    messagesLD.value = messageList
//                    Log.d(
//                        TAG,
//                        "onDataChange: Количество сообщений у текущего пользователя: ${messageList.count()}"
//                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
//        calculateCountUsersMessagesList()
    }

    fun calculateCountUsersMessagesList() {
        val countCurrentUserMessages = 0
        referenceMessages.child(otherUserId).child(currentUserId)
            .child(pathListOfMessages)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listOfOtherUserMessages: MutableList<Message> = mutableListOf()
                    for (snap in snapshot.children) {
                        snap.getValue(Message::class.java)
                            ?.let {
                                listOfOtherUserMessages.add(it)
                            }
                    }
                    Log.d(
                        TAG,
                        "onDataChange: Количество сообщений у другого пользователя: ${listOfOtherUserMessages.count()}"
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun setReadMessageStatusForDelivering(status: String) {
        //Выставляем статус сообщений при отправке. Прилетает false
        referenceMessages
            .child(otherUserId)
            .child(currentUserId)
            .child(pathListOfMessages)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updateMessageMap: MutableMap<String, String> = mutableMapOf()
                    for (snap in snapshot.children) {
                        updateMessageMap.put("read", status)
                        snap.ref.updateChildren(updateMessageMap as Map<String, Any>)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun setReadMessageStatusForReading(status: String) {
        //Выставляем статус себе сообщений при открытии диалога. Прилетает true
        referenceMessages
            .child(currentUserId)
            .child(otherUserId)
            .child(pathListOfMessages)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val updateMessageMap: MutableMap<String, String> =
                        mutableMapOf() //Карта обновления сообщения после прочтения пользователем
                    for (snap in snapshot.children) {
                        updateMessageMap.put("read", status)
                        snap.ref.updateChildren(updateMessageMap as Map<String, Any>)
                    }
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
            .child(pathListOfMessages)
            .push()
            .setValue(message)
            .addOnSuccessListener {
                referenceMessages
                    .child(message.recieverId)
                    .child(message.senderId)
                    .child(pathListOfMessages)
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
//        setReadMessageStatusForDelivering("false")
    }

    fun setTyping() {
        object : CountDownTimer(3000, 100) {
            override fun onFinish() {
                referenceMessages.child(currentUserId).child(otherUserId)
                    .child(pathTyping)
                    .setValue(false)
            }

            override fun onTick(millisUntilFinished: Long) {
                referenceMessages.child(currentUserId).child(otherUserId)
                    .child(pathTyping)
                    .setValue(true)
            }
        }.start()
    }

    fun setUserOnline(isOnline: Boolean) {
        referenceUsers.child(currentUserId).child("online").setValue(isOnline)
    }
}