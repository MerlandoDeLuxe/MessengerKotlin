package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class UserInfoActivity : AppCompatActivity() {
    private val EXTRA_NAME = "name"
    private val EXTRA_SURNAME = "surname"
    private val EXTRA_AGE = "age"
    private val EXTRA_CURRENT_USER_ID = "current_id"
    private val EXTRA_OTHER_USER_ID = "other_id"
    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var age: String
    private lateinit var imageViewUserPhoto: ImageView
    private lateinit var imageViewSendMessage: ImageView
    private lateinit var textViewUserName: TextView
    private lateinit var textViewUserSurname: TextView
    private lateinit var textViewUserAge: TextView
    private lateinit var textViewUserInfo: TextView
    private lateinit var imageViewUserStatus: ImageView
    private lateinit var currentUserId: String
    private lateinit var otherUserId: String
    private lateinit var adapter: UserAdapter
    private lateinit var viewModelFactory: UserInfoViewModelFactory
    private lateinit var viewModel: UserInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        name = intent.getStringExtra(EXTRA_NAME).toString()
        surname = intent.getStringExtra(EXTRA_SURNAME).toString()
        age = intent.getStringExtra(EXTRA_AGE).toString()
        currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID).toString()
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID).toString()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        initializeAllElements()

        textViewUserName.text = name
        textViewUserSurname.text = surname
        textViewUserAge.text = age
        textViewUserInfo.text =
            "Съешь ещё этих мягких французских булок, да выпей же чаю. Эй, жлоб! Где туз? Прячь юных съёмщиц в шкаф"

        setupOnClickListeners()
        observeViewModel()
        if (currentUserId.equals(otherUserId)) {
            imageViewSendMessage.visibility = ImageView.INVISIBLE
            imageViewUserStatus.visibility = ImageView.INVISIBLE
        }
    }

    fun setupOnClickListeners() {
        imageViewSendMessage.setOnClickListener({
            val intent = ChatActivity().newIntent(this, currentUserId, otherUserId)
            startActivity(intent)
        })
    }

    fun observeViewModel() {
        //Вызываем другого пользователя
        viewModel.getOtherUserData()


        viewModel.otherUserLD.observe(this, {
            var backgroundIntRes: Int
            if (it.online) {
                backgroundIntRes = R.drawable.circle_green_online
            } else backgroundIntRes = R.drawable.circle_red_offline

            var drawable =
                ContextCompat.getDrawable(this, backgroundIntRes)
            imageViewUserStatus.setImageDrawable(drawable)
        })
    }

    fun newIntent(
        context: Context,
        currentUserId: String,
        otherUserId: String,
        name: String,
        surname: String,
        age: String,
    ): Intent {
        val intent = Intent(context, UserInfoActivity::class.java)
        intent.putExtra(EXTRA_NAME, name)
        intent.putExtra(EXTRA_SURNAME, surname)
        intent.putExtra(EXTRA_AGE, age)
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
        return intent
    }

    private fun initializeAllElements() {
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto)
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage)
        textViewUserName = findViewById(R.id.textViewUserName)
        textViewUserSurname = findViewById(R.id.textViewUserSurname)
        textViewUserAge = findViewById(R.id.textViewUserAge)
        textViewUserInfo = findViewById(R.id.textViewUserInfo)
        imageViewUserStatus = findViewById(R.id.imageViewUserStatus)
        adapter = UserAdapter()
        viewModelFactory = UserInfoViewModelFactory(otherUserId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserInfoViewModel::class.java)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUserOnline(false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserOnline(true)
    }
}