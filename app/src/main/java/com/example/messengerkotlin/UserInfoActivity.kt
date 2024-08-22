package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.Serializable
import kotlin.math.log

class UserInfoActivity : AppCompatActivity() {
    private val TAG = "UserInfoActivity"
    private val EXTRA_NAME = "name"
    private val EXTRA_SURNAME = "surname"
    private val EXTRA_AGE = "age"
    private val EXTRA_CURRENT_USER_ID = "current_id"
    private val EXTRA_OTHER_USER_ID = "other_id"
    private val EXTRA_USER_INFO = "userInfo"
    private val EXTRA_USER = "extra_user"
    private val EXTRA_USER_ONLINE = "online"
    private val EXTRA_USER_MAIN_PHOTO = "userMainPhoto"

    private lateinit var imageViewUserPhoto: ImageView
    private lateinit var imageViewSendMessage: ImageView
    private lateinit var textViewUserName: TextView
    private lateinit var textViewUserSurname: TextView
    private lateinit var textViewUserAge: TextView
    private lateinit var textViewUserInfo: TextView
    private lateinit var imageViewUserStatus: ImageView
    private lateinit var imageViewToYourProfile: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var name: String
    private lateinit var surname: String
    private lateinit var age: String
    private lateinit var currentUserId: String
    private lateinit var otherUserId: String
    private lateinit var userInfo: String
    private var online = false
    private lateinit var userMainPhoto: String
    private lateinit var adapter: UserPhotoAdapter
    private lateinit var viewModelFactory: UserInfoViewModelFactory
    private lateinit var viewModel: UserInfoViewModel
    private lateinit var recycleViewUserPhoto: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d(TAG, "onCreate: UserInfoActivity 1, online = $online")
        name = intent.getStringExtra(EXTRA_NAME).toString()
        surname = intent.getStringExtra(EXTRA_SURNAME).toString()
        age = intent.getStringExtra(EXTRA_AGE).toString()
        currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID).toString()
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID).toString()
        userInfo = intent.getStringExtra(EXTRA_USER_INFO).toString()
        online = intent.extras?.getBoolean(EXTRA_USER_ONLINE) ?: false
        userMainPhoto = intent.getStringExtra(EXTRA_USER_MAIN_PHOTO).toString()

        Log.d(TAG, "onCreate: UserInfoActivity 2, online = $online")
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        initializeAllElements()

        textViewUserName.text = name
        textViewUserSurname.text = surname
        textViewUserAge.text = age
        textViewUserInfo.text = userInfo

        setupOnClickListeners()
        observeViewModel()
        showOtherUserInfo()
        if (currentUserId.equals(otherUserId)) {
            imageViewSendMessage.visibility = ImageView.INVISIBLE
            imageViewUserStatus.visibility = ImageView.INVISIBLE
            showCurrentUserInfo()
        } else {
            imageViewToYourProfile.visibility = ImageView.INVISIBLE
        }
    }

    fun setupOnClickListeners() {
        imageViewSendMessage.setOnClickListener({
            val intent = ChatActivity().newIntent(this, currentUserId, otherUserId, userMainPhoto)
            startActivity(intent)
        })

        imageViewToYourProfile.setOnClickListener({
            val intent = MyProfileActivity().newIntent(
                this,
                currentUserId,
                otherUserId,
                textViewUserName.text.toString(),
                textViewUserSurname.text.toString(),
                textViewUserAge.text.toString(),
                textViewUserInfo.text.toString()
            )
            startActivity(intent)
        })
        //=================================================================================

    }

    fun observeViewModel() {
        viewModel.collectionUserPhotoLD.observe(this) {
            //Здесь отправляем в адаптер только адреса для отображения. Имя файлов для этого не требуется
            val tempListOfPhotoUri: MutableList<Uri> = mutableListOf()
            for ((key, value) in it) {
                tempListOfPhotoUri.add(value)
            }
            Log.d(TAG, "observeViewModel: вызов адаптера")
            adapter.urlUserPhotoList = tempListOfPhotoUri

        }
        if (!userMainPhoto.equals("")){
            Glide.with(this)
                .load(userMainPhoto)
                .into(imageViewUserPhoto)
        }
        else if(userMainPhoto.equals("")){
            Glide.with(this)
                .load(R.drawable.colt_bg_logo)
                .into(imageViewUserPhoto)
        }

        viewModel.isPhotosStillLoading.observe(this, {
            if (it){
                progressBar.visibility = ProgressBar.VISIBLE
            }
            else{
                progressBar.visibility = ProgressBar.INVISIBLE
            }
        })
    }

    fun showCurrentUserInfo() {
        //Вызываем текущего пользователя чтобы передать обновить его данные на этой странице и затем передать дальше в d MyProfileActivity
        viewModel.getCurrentUser()
        viewModel.currentUserLD.observe(this) {
            if (it != null) {
                textViewUserName.text = it.name
                textViewUserSurname.text = it.surname
                Log.d(TAG, "observeViewModel: возраст = ${it.age}")
                if (it.age == 0) {
                    textViewUserAge.visibility = TextView.INVISIBLE
                } else {
                    textViewUserAge.visibility = TextView.VISIBLE
                    textViewUserAge.text = it.age.toString()
                }
                textViewUserInfo.text = it.userInfo
            }
        }
    }

    fun showOtherUserInfo() {
        //Вызываем другого пользователя
        viewModel.getOtherUserData()
        viewModel.otherUserLD.observe(this) {

            textViewUserName.text = it.name
            textViewUserSurname.text = it.surname
            textViewUserAge.text = it.age.toString()

            var backgroundIntRes: Int
            if (online) {
                backgroundIntRes = R.drawable.circle_green_online
            } else backgroundIntRes = R.drawable.circle_red_offline

            var drawable =
                ContextCompat.getDrawable(this, backgroundIntRes)
            imageViewUserStatus.setImageDrawable(drawable)
            Log.d(TAG, "onCreate: UserInfoActivity 4, online = $online")
        }
    }

    fun newIntent(
        context: Context,
        currentUserId: String,
        otherUserId: String,
        name: String,
        surname: String,
        age: String,
        userInfo: String,
        online: Boolean,
        userMainPhoto: String
    ): Intent {
        val intent = Intent(context, UserInfoActivity::class.java)
        intent.putExtra(EXTRA_NAME, name)
        intent.putExtra(EXTRA_SURNAME, surname)
        intent.putExtra(EXTRA_AGE, age)
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
        intent.putExtra(EXTRA_USER_INFO, userInfo)
        intent.putExtra(EXTRA_USER_ONLINE, online)
        intent.putExtra(EXTRA_USER_MAIN_PHOTO, userMainPhoto)
        return intent
    }

    fun newIntent(context: Context, user: User) {
        val intent = Intent(context, UserInfoActivity::class.java)
        intent.putExtra(EXTRA_USER, user as Serializable)
        startActivity(intent)
    }

    private fun initializeAllElements() {
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto)
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage)
        textViewUserName = findViewById(R.id.textViewUserName)
        textViewUserSurname = findViewById(R.id.textViewUserSurname)
        textViewUserAge = findViewById(R.id.textViewUserAge)
        textViewUserInfo = findViewById(R.id.textViewUserInfo)
        imageViewUserStatus = findViewById(R.id.imageViewUserStatus)
        imageViewToYourProfile = findViewById(R.id.imageViewToYourProfile)
        recycleViewUserPhoto = findViewById(R.id.recycleViewUserPhoto)
        progressBar = findViewById(R.id.progressBar)

        adapter = UserPhotoAdapter(localClassName)
        recycleViewUserPhoto.adapter = adapter
        recycleViewUserPhoto.layoutManager = GridLayoutManager(this, 2)

        viewModelFactory = UserInfoViewModelFactory(otherUserId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserInfoViewModel::class.java)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUserOnline(false)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        viewModel.setUserOnline(true)
        viewModel.getAllUserPhotos()
    }
}