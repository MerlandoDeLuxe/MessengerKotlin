package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatActivity : AppCompatActivity() {
    private val EXTRA_CURRENT_USER_ID = "current_id"
    private val EXTRA_OTHER_USER_ID = "other_id"
    private val TAG = "ChatActivity"
    private val EXTRA_USER_MAIN_PHOTO = "userMainPhoto"

    private lateinit var textViewTitle: TextView;
    private lateinit var imageViewUserStatus: ImageView;
    private lateinit var recycleViewMessages: RecyclerView;
    private lateinit var editTextMessage: EditText;
    private lateinit var imageViewSendMessage: ImageView;
    private lateinit var adapter: MessageAdapter
    private lateinit var imageViewUserPhotoIcon: ImageView
    private lateinit var textViewTyping: TextView

    private lateinit var currentUserId: String
    private lateinit var otherUserId: String
    private lateinit var userMainPhoto: String

    private lateinit var viewModel: ChatViewModel
    private lateinit var viewModelFactory: ChatViewModelFactory

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
        userMainPhoto = intent.getStringExtra(EXTRA_USER_MAIN_PHOTO).toString()

        initializeAllElements()
        setupOnClickListeners()
        observeViewModel()
    }

    fun setupOnClickListeners() {
        imageViewSendMessage.setOnClickListener {
            if (editTextMessage.text.toString().trim().isNotEmpty()) {
                val message =
                    Message(currentUserId, otherUserId, editTextMessage.text.toString().trim(),"false")
                viewModel.sendMessage(message)
                editTextMessage.text.clear()
            }
        }

        editTextMessage.addTextChangedListener {
            Log.d(TAG, "observeViewModel: Печатает...")
            viewModel.setTyping()
        }
    }

    fun observeViewModel() {
        //сначала получаем сообщения
        viewModel.getAllMessagesBetweenUsers()
        //затем если они получены и livedata изменилась, то отправляем из нее значения в адаптер для отображения
        viewModel.messagesLD.observe(this) {
            adapter.messages = it
            if (adapter.itemCount > 0) {
                recycleViewMessages.scrollToPosition(adapter.itemCount - 1)
            }
        }
        //Если вдруг возникла ошибка, то:
        viewModel.errorLD.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        //=======================================================================================
        //Сначала получаем пользователя, с которым общаемся
        viewModel.getOtherUser()
        //установка имени и фамилии пользователя, с которым общаемся в текстовое поле сверху
        viewModel.otherUserLD.observe(this) {
            textViewTitle.text = String.format("%s %s", it.name, it.surname)

            var backgroundIntRes: Int
            if (it.online) {
                backgroundIntRes = R.drawable.circle_green_online
            } else backgroundIntRes = R.drawable.circle_red_offline

            var drawable =
                ContextCompat.getDrawable(this, backgroundIntRes)
            imageViewUserStatus.setImageDrawable(drawable)
        }
        //=======================================================================================
        //Затем получаем иконку пользователя для чата:
        if (!userMainPhoto.equals("")) {
            Glide.with(this)
                .load(userMainPhoto)
                .into(imageViewUserPhotoIcon)
        }
        //=======================================================================================
        //Чтобы список сообщений проматывался вниз при открытии клавиатуры
        recycleViewMessages.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                if (adapter.itemCount > 0) {
                    recycleViewMessages.postDelayed(object : Runnable {
                        override fun run() {
                            recycleViewMessages.smoothScrollToPosition(
                                recycleViewMessages.adapter?.itemCount?.minus(
                                    1
                                ) ?: 0
                            )
                        }
                    }, 100)
                }
            }
        })

//============================================================================
        viewModel.isUserTyping.observe(this,{
            if (it){
                Log.d(TAG, "observeViewModel: 11")
                textViewTyping.text = getString(R.string.typing)
            }
            else{
                Log.d(TAG, "observeViewModel: 22")
                textViewTyping.text = ""
            }
        })
    }

    fun newIntent(
        context: Context,
        currentUserId: String,
        otherUserId: String,
        userMainPhoto: String
    ): Intent {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
        intent.putExtra(EXTRA_USER_MAIN_PHOTO, userMainPhoto)
        return intent
    }

    private fun initializeAllElements() {
        textViewTitle = findViewById(R.id.textViewTitle);
        imageViewUserStatus = findViewById(R.id.imageViewUserStatus);
        recycleViewMessages = findViewById(R.id.recycleViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
        imageViewUserPhotoIcon = findViewById(R.id.imageViewUserPhotoIcon)
        textViewTyping = findViewById(R.id.textViewTyping)

        adapter = MessageAdapter(currentUserId)
        recycleViewMessages.adapter = adapter
        recycleViewMessages.layoutManager = LinearLayoutManager(this)
        viewModelFactory = ChatViewModelFactory(currentUserId, otherUserId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatViewModel::class.java)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        viewModel.setUserOnline(false)
        Log.d(TAG, "onPause: Вызов viewModel.setOnlineWithTargetUser(false)")
        viewModel.setOnlineWithTargetUser(false)
//        viewModel.setReadMessageStatusForReading("false")
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserOnline(true)
        Log.d(TAG, "onPause: Вызов viewModel.setOnlineWithTargetUser(true)")
        viewModel.setOnlineWithTargetUser(true)
//        viewModel.setReadMessageStatusForReading("true")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onPause: Вызов viewModel.setOnlineWithTargetUser(false)")
        viewModel.setOnlineWithTargetUser(false)
//        viewModel.setReadMessageStatusForReading("false")
    }
}