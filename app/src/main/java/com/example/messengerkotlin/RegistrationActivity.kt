package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class RegistrationActivity : AppCompatActivity() {
    private val TAG: String = "RegistrationActivity"
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var editTextRegistrationEmail: EditText
    private lateinit var editTextRegistrationPassword: EditText
    private lateinit var editTextRegistrationName: EditText
    private lateinit var editTextRegistrationSurname: EditText
    private lateinit var editTextRegistrationAge: EditText
    private lateinit var textViewRegistrtionComplete: TextView
    private lateinit var buttonRegistration: Button
    private val passwordLength = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeAllElements()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        setupOnCLickListeners()
        observeViewModel()
    }

    private fun observeViewModel(){
        viewModel.wrongAuthTextLD.observe(this) {
            if (it != null) {
                Toast.makeText(
                    this,
                    it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
//============================================================================================
        viewModel.userLD.observe(this) {
            if (it != null) {
                Toast.makeText(
                    this,
                    R.string.user_created,
                    Toast.LENGTH_SHORT
                ).show()
                textViewRegistrtionComplete.visibility = View.VISIBLE

                val intent =
                    LoginActivity().newIntent(this, editTextRegistrationEmail.text.toString())
                startActivity(intent)
                finish()
            } else {
                Log.d(TAG, "observeViewModel: Результат создания нового пользователя = $it")
            }
        }
    }

    private fun setupOnCLickListeners(){
        buttonRegistration.setOnClickListener {
            if (editTextRegistrationEmail.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.wrong_email_registration,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTextRegistrationPassword.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.wrong_password_registration,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTextRegistrationPassword.text.toString()
                    .trim().length < passwordLength
            ) {
                Toast.makeText(
                    this,
                    String.format(
                        getString(R.string.wrong_password_length_registration),
                        passwordLength
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTextRegistrationName.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.wrong_name_registration,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTextRegistrationSurname.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.wrong_surname_registration,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (editTextRegistrationAge.text.toString().trim().isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.wrong_age_registration,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.createUser(
                    editTextRegistrationEmail.text.toString().trim(),
                    editTextRegistrationPassword.text.toString().trim(),
                    editTextRegistrationName.text.toString().trim(),
                    editTextRegistrationSurname.text.toString().trim(),
                    editTextRegistrationAge.text.toString().toInt()
                )
            }
        }
    }

    fun newIntent(context: Context) = Intent(context, RegistrationActivity::class.java)

    private fun initializeAllElements(){
        editTextRegistrationEmail = findViewById(R.id.editTextRegistrationEmail)
        editTextRegistrationPassword = findViewById(R.id.editTextRegistrationPassword)
        editTextRegistrationName = findViewById(R.id.editTextRegistrationName)
        editTextRegistrationSurname = findViewById(R.id.editTextRegistrationSurname)
        editTextRegistrationAge = findViewById(R.id.editTextRegistrationAge)
        textViewRegistrtionComplete = findViewById(R.id.textViewRegistrtionComplete)
        buttonRegistration = findViewById(R.id.buttonRegistration)
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
    }
}