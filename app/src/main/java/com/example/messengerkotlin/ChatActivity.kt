package com.example.messengerkotlin

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {
    private lateinit var textViewTitle: TextView;
    private lateinit var imageViewUserStatus: ImageView;
    private lateinit var recycleViewMessages: RecyclerView;
    private lateinit var editTextMessage: EditText;
    private lateinit var imageViewSendMessage: ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeAllElements()
    }

    private fun initializeAllElements() {
        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewUserStatus = findViewById(R.id.imageViewUserStatus);
        recycleViewMessages = findViewById(R.id.recycleViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
    }
}