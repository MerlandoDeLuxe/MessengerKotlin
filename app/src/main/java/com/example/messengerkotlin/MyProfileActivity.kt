package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class MyProfileActivity : AppCompatActivity() {
    private val TAG = "MyProfileActivity"
    private val EXTRA_NAME = "name"
    private val EXTRA_SURNAME = "surname"
    private val EXTRA_AGE = "age"
    private val EXTRA_USER_INFO = "userInfo"

    private lateinit var editTextYourName: EditText
    private lateinit var editTextYourSurname: EditText
    private lateinit var editTextYourAge: EditText
    private lateinit var editTextYourInfo: EditText
    private lateinit var buttonSaveAndBack: ImageView
    private lateinit var imageViewSetChanges: ImageView

    private lateinit var viewModel: MyProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        initializeAllElements()
        editTextYourName.setText(intent.getStringExtra(EXTRA_NAME))
        editTextYourSurname.setText(intent.getStringExtra(EXTRA_SURNAME))
        editTextYourAge.setText(intent.getStringExtra(EXTRA_AGE))
        editTextYourInfo.setText(intent.getStringExtra(EXTRA_USER_INFO))
        observeViewModel()
        setupOnClickListeners()
    }

    fun observeViewModel() {
        viewModel.checkAdminUser()
        viewModel.isUserAdminLD.observe(this, {
            if (it) {
                imageViewSetChanges.visibility = ImageView.VISIBLE
            } else {
                imageViewSetChanges.visibility = ImageView.INVISIBLE
            }
        })
    }

    fun setupOnClickListeners() {
        buttonSaveAndBack.setOnClickListener {
            saveUserData()
            finish()
        }

        imageViewSetChanges.setOnClickListener({
            viewModel.updateAllUsers()
        })
    }

    fun saveUserData() {
        var age = editTextYourAge.text.toString().trim()
        Log.d(TAG, "setupOnClickListeners: $age")
        if (age.isEmpty()) {
            age = "0"
        }

        viewModel.saveUserData(
            editTextYourName.text.toString().trim(),
            editTextYourSurname.text.toString().trim(),
            age.toInt(),
            editTextYourInfo.text.toString().trim()
        )
    }

    fun newIntent(
        context: Context,
        name: String,
        surname: String,
        age: String,
        userInfo: String
    ): Intent {
        val intent = Intent(context, MyProfileActivity::class.java)
        intent.putExtra(EXTRA_NAME, name)
        intent.putExtra(EXTRA_SURNAME, surname)
        intent.putExtra(EXTRA_AGE, age)
        intent.putExtra(EXTRA_USER_INFO, userInfo)
        return intent
    }

    fun initializeAllElements() {
        editTextYourName = findViewById(R.id.editTextYourName)
        editTextYourSurname = findViewById(R.id.editTextYourSurname)
        editTextYourAge = findViewById(R.id.editTextYourAge)
        editTextYourInfo = findViewById(R.id.editTextYourInfo)
        buttonSaveAndBack = findViewById(R.id.buttonSaveAndBack)
        imageViewSetChanges = findViewById(R.id.imageViewSetChanges)
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        viewModel.setUserOnline(false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserOnline(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveUserData()
        Log.d(TAG, "onDestroy: ")
    }
}