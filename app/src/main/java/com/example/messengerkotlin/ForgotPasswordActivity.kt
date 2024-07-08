package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var editTextTextEmailAddressForRecovery: EditText
    private lateinit var buttonPasswordRecovery: Button
    private val EXTRA_EMAIL: String = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeAllElements()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0F5E93")))
        editTextTextEmailAddressForRecovery.setText(intent.getStringExtra(EXTRA_EMAIL))
        //==================================================================================
        setupOnClickListeners()
        observeViewModel()
    }

    private fun setupOnClickListeners() {
        buttonPasswordRecovery.setOnClickListener {
            if (editTextTextEmailAddressForRecovery.text.toString().trim().isNotEmpty()) {
                viewModel.passwordRecovery(
                    editTextTextEmailAddressForRecovery.text.toString().trim()
                )
            } else {
                Toast.makeText(this,
                    R.string.wrong_email_registration,
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel(){
        viewModel.isSendedLD.observe(this) {
            if (it != null) {
                Toast.makeText(
                    this,
                    R.string.success_send_email,
                    Toast.LENGTH_LONG
                ).show()

                val intent = LoginActivity().newIntent(
                    this,
                    editTextTextEmailAddressForRecovery.text.toString().trim()
                )
                startActivity(intent)
                finish()
            }
        }
    }

    fun newIntent(context: Context, email: String): Intent {
        val intent = Intent(context, ForgotPasswordActivity::class.java)
        intent.putExtra(EXTRA_EMAIL, email)
        return intent
    }

    private fun initializeAllElements() {
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        editTextTextEmailAddressForRecovery = findViewById(R.id.editTextTextEmailAddressForRecovery)
        buttonPasswordRecovery = findViewById(R.id.buttonPasswordRecovery)
    }
}