package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyProfileActivity : AppCompatActivity() {
    private val TAG = "MyProfileActivity"
    private val EXTRA_NAME = "name"
    private val EXTRA_SURNAME = "surname"
    private val EXTRA_AGE = "age"
    private val EXTRA_USER_INFO = "userInfo"
    private val EXTRA_CURRENT_USER_ID = "current_id"
    private val EXTRA_OTHER_USER_ID = "other_id"

    private lateinit var editTextYourName: EditText
    private lateinit var editTextYourSurname: EditText
    private lateinit var editTextYourAge: EditText
    private lateinit var editTextYourInfo: EditText
    private lateinit var buttonSaveAndBack: ImageView
    private lateinit var imageViewSetChanges: ImageView
    private lateinit var imageViewChangeUserPhoto: ImageView

    private lateinit var viewModel: MyProfileViewModel
    private lateinit var adapter: UserPhotoAdapter
    private lateinit var recycleViewUserPhoto: RecyclerView

    private lateinit var currentUserId: String
    private lateinit var otherUserId: String

    //    private lateinit var cropImageView: CropImageView
    lateinit var uri: Uri

    private lateinit var imageViewFromCropImage: ImageView

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
        currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID).toString()
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID).toString()
        editTextYourName.setText(intent.getStringExtra(EXTRA_NAME))
        editTextYourSurname.setText(intent.getStringExtra(EXTRA_SURNAME))
        editTextYourAge.setText(intent.getStringExtra(EXTRA_AGE))
        editTextYourInfo.setText(intent.getStringExtra(EXTRA_USER_INFO))
        observeViewModel()
        setupOnClickListeners()

    }

    fun observeViewModel() {
        viewModel.checkAdminUser()
        viewModel.isUserAdminLD.observe(this) {
            if (it) {
                imageViewSetChanges.visibility = ImageView.VISIBLE
            } else {
                imageViewSetChanges.visibility = ImageView.INVISIBLE
            }
        }

        viewModel.userPhotoLD.observe(this) {
                //Здесь отправляем в адаптер только адреса для отображения. Имя файлов для этого не требуется
                val tempListOfPhotoUri: MutableList<Uri> = mutableListOf()
                for ((key, value) in it) {
                    tempListOfPhotoUri.add(value)
                }
            Log.d(TAG, "observeViewModel: вызов адаптера")
                adapter.urlUserPhotoList = tempListOfPhotoUri
        }
    }

    fun setupOnClickListeners() {
        buttonSaveAndBack.setOnClickListener {
            saveUserData()
            finish()
        }
        //==============================================================================
        imageViewSetChanges.setOnClickListener({
            viewModel.updateAllUsers()
        })

        //==============================================================================
        imageViewChangeUserPhoto.setOnClickListener({

        })

        adapter.onDeletePhotoClickListener {
            viewModel.checkSelectedUserPhotoForDelete(it)
            Log.d(TAG, "setupOnClickListeners: $it")
        }

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
        currentUserId: String,
        otherUserId: String,
        name: String,
        surname: String,
        age: String,
        userInfo: String
    ): Intent {
        val intent = Intent(context, MyProfileActivity::class.java)
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
        intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
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
        imageViewChangeUserPhoto = findViewById(R.id.imageViewChangeUserPhoto)


        recycleViewUserPhoto = findViewById(R.id.recycleViewUserPhoto)
        adapter = UserPhotoAdapter()
        recycleViewUserPhoto.adapter = adapter
        recycleViewUserPhoto.layoutManager = GridLayoutManager(this, 1)
//        imageViewFromCropImage = findViewById(R.id.imageViewFromCropImage)
//        cropImageView = findViewById(com.canhub.cropper.R.id.cropImageView)


//
        viewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
//
//        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
//            it?.let { uri ->
//               // imageViewFromCropImage.setImageURI(uri)
//                viewModel.changeUserPhoto(uri)
//            }
//        }
//
//        uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/messengerkotlin-f6258.appspot.com/o/UserPhotoGallery%2F6v9ugEJ03iP0fkMxXkgmoBqSzzG3%2FOpenPhotos%2F5035634689829044033?alt=media&token=b704c40e-812b-45ae-8d4c-5ada2d69345a")
//        val draweeView = findViewById<SimpleDraweeView>(R.id.my_image_view) as SimpleDraweeView
//        draweeView.setImageURI(uri)
//
//
//        cropImageView.setImageUriAsync(uri)
//        cropImageView.setImageUriAsync(uri)
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