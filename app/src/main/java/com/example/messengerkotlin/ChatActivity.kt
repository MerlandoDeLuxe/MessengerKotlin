package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {
    private val EXTRA_CURRENT_USER_ID = "current_id"
    private val EXTRA_OTHER_USER_ID = "other_id"
    private val TAG = "ChatActivity"
    private lateinit var textViewTitle: TextView;
    private lateinit var imageViewUserStatus: ImageView;
    private lateinit var recycleViewMessages: RecyclerView;
    private lateinit var editTextMessage: EditText;
    private lateinit var imageViewSendMessage: ImageView;
    private lateinit var adapter: MessageAdapter

    private lateinit var currentUserId: String
    private lateinit var otherUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID).toString()
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID).toString()
        initializeAllElements()

        val messages: MutableList<Message> = mutableListOf()
        for (i in 0..10) {
            if (i % 2 == 0) {
                messages.add(i, Message("Мое тестовое сообщение", currentUserId, otherUserId))
            }
            else{
                messages.add(i, Message("Чужое тестовое сообщение", otherUserId, currentUserId))
            }
        }
        adapter.messages = messages
        recycleViewMessages.adapter = adapter
        recycleViewMessages.layoutManager = LinearLayoutManager(this)
    }

    fun newIntent(context: Context, currentUserId: String, otherUserId: String): Intent {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
        return intent
    }

    private fun initializeAllElements() {
        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewUserStatus = findViewById(R.id.imageViewUserStatus);
        recycleViewMessages = findViewById(R.id.recycleViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        adapter = MessageAdapter(currentUserId)
    }
}