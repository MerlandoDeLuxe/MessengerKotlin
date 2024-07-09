package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListOfUsersActivity : AppCompatActivity() {
    private val TAG: String = "ListOfUsersActivity"
    private lateinit var viewModel: ListOfUsersViewModel
    private val EXTRA_EMAIL: String = "email"
    private lateinit var adapter: UserAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_of_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerViewUsers)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#800F5E93")))
        initializeAllElements()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.userLD.observe(this) {
            if (it == null) {
                Log.d(TAG, "observeViewModel: useremail = ${viewModel.userEmail}")
                finish()
                val intent = LoginActivity().newIntent(this, viewModel.userEmail)
                startActivity(intent)
            }
        }
        viewModel.getUsersFromDb()
        viewModel.userListLD.observe(this, {
            adapter.userList = it
        })
    }

    fun newIntent(context: Context, email: String): Intent {
        val intent = Intent(context, ListOfUsersActivity::class.java)
        intent.putExtra(EXTRA_EMAIL, email)
        return intent
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemLogout) {
            viewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeAllElements() {
        viewModel = ViewModelProvider(this).get(ListOfUsersViewModel::class.java)
        adapter = UserAdapter()
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
    }
}