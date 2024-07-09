package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class LoginActivity : AppCompatActivity() {
    private val TAG: String = "LoginActivity"
    lateinit var editTextTextEmailAddress: EditText
    lateinit var editTextTextPassword: EditText
    lateinit var loginButton: Button
    lateinit var textViewForgotPassword: TextView
    lateinit var textViewRegistration: TextView
    lateinit var viewModel: LoginViewModel
    private val EXTRA_EMAIL: String = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeAllElements()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        editTextTextEmailAddress.setText(intent.getStringExtra(EXTRA_EMAIL))
        //========================================================================================
        setupOnClickListeners();
        observeViewModel()
    }

    private fun setupOnClickListeners() {
        loginButton.setOnClickListener {
            if (editTextTextEmailAddress.text.toString().trim().isNotEmpty() &&
                editTextTextPassword.text.toString().trim().isNotEmpty()
            ) {
                //Если почта и пароль заполнены, то тогда пытаемся логинить пользователя
                viewModel.loginUser(
                    editTextTextEmailAddress.text.toString().trim(),
                    editTextTextPassword.text.toString().trim()
                )
            } else {
                Toast.makeText(
                    this,
                    R.string.login_or_password_is_empty,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        //========================================================================================
        textViewRegistration.setOnClickListener {
            val intent = RegistrationActivity().newIntent(this)
            startActivity(intent)
        }
        //========================================================================================
        textViewForgotPassword.setOnClickListener {
            val intent = ForgotPasswordActivity().newIntent(
                this,
                editTextTextEmailAddress.text.toString().trim()
            )
            startActivity(intent)
        }
    }

    fun observeViewModel() {
        viewModel.authStateListener()

        viewModel.authErrorMessageLD.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.firebaseUserLD.observe(this) {
            if (it != null) {
                val intent = ListOfUsersActivity().newIntent(this, EXTRA_EMAIL)
                startActivity(intent)
                editTextTextPassword.setText("");
                finish()
            }
        }
    }

    fun newIntent(context: Context, email: String?): Intent {
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra(EXTRA_EMAIL, email)
        return intent
    }

    fun initializeAllElements() {
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress)
        editTextTextPassword = findViewById(R.id.editTextTextPassword)
        loginButton = findViewById(R.id.loginButton)
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword)
        textViewRegistration = findViewById(R.id.textViewRegistration)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }
}