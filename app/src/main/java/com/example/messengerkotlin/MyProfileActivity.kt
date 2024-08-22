package com.example.messengerkotlin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import java.util.concurrent.ThreadLocalRandom


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
    private lateinit var imageViewShowPhoto: ImageView
    private lateinit var imageViewUserPhoto: ImageView
    private lateinit var progressBar: ProgressBar

    private lateinit var viewModel: MyProfileViewModel
    private lateinit var adapter: UserPhotoAdapter
    private lateinit var recycleViewUserPhoto: RecyclerView

    private lateinit var currentUserId: String
    private lateinit var otherUserId: String

    private val SELECT_PICTURE = 200
    private lateinit var selectedImageBitmap: Bitmap

    private val GalleryPick = 1
    private val CAMERA_REQUEST = 100
    private val STORAGE_REQUEST = 200
    private val IMAGEPICK_GALLERY_REQUEST = 300
    private val IMAGE_PICKCAMERA_REQUEST = 400
    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private lateinit var imageUri: Uri

    private lateinit var preferences: SharedPreferences
    private lateinit var myEdit: SharedPreferences.Editor
    private lateinit var user_photo_Uri: String

//    //Для запуска новой активити чтобы выбрать фотку с устройства
//    val launchSomeActivity = registerForActivityResult(
//        ActivityResultContracts
//            .StartActivityForResult()
//    ) {
//        if (it.resultCode == Activity.RESULT_OK) {
//            val intentData = it.data
//            if (intentData != null && intentData.data != null) {
//                val selectedImageUri = intentData.data
//
//                try {
//                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
//                        this.contentResolver,
//                        selectedImageUri
//                    )
//                } catch (e: IOException) {
//                    Log.d(TAG, "onCreate: Ошибка: ${e.message}")
//                }
//                imageViewShowPhoto.setImageBitmap(selectedImageBitmap)
//            }
//        }
//    }

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(9, 16)
                .getIntent(this@MyProfileActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

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

        preferences = applicationContext.getSharedPreferences("user_photo", Context.MODE_PRIVATE)
        user_photo_Uri = preferences.getString("user_photo", "").toString()
        Log.d(TAG, "onCreate: user_photo_Uri = $user_photo_Uri")
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }
        //Камеру нужно доделать отсюда: https://www.youtube.com/watch?v=12_iKwGIP64
        //после изучения Compose
//        setContent {
//            android.graphics.Camera()
//        }

        observeViewModel()
        setupOnClickListeners()

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {

            it?.let { uri ->
//                imageViewShowPhoto.setImageURI(uri)

                viewModel.addNewUserPhoto(uri)
            }
        }
    }

//    fun imageChooser() {
//        val intent = Intent()
//        intent.setType("image/*")
//        intent.setAction(Intent.ACTION_GET_CONTENT)
////        startActivityForResult(Intent.createChooser(intent, "Выберите каритнку"), SELECT_PICTURE)
//        launchSomeActivity.launch(intent)
//    }


//    //    Deprecated метод для вызова активити с галереей
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                val selectedImageUri = data?.data
//                if (null != selectedImageUri) {
//                    imageViewShowPhoto.setImageURI(selectedImageUri)
//                }
//            }
//        }
//    }

//    fun showImageDialog() {
//        val options: Array<String> = arrayOf("Camera", "Gallery")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Откуда выбрать картинку: ")
//        builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
//            if (which ==0){
//                Toast.makeText(this, "нет прав", Toast.LENGTH_SHORT).show()
//            }
//        })
//        builder.create().show()
//    }


//    fun pickFromGallery(){
//        CropImage.activity().start
//    }

    fun observeViewModel() {
        viewModel.checkAdminUser()
        viewModel.isUserAdminLD.observe(this) {
            if (it) {
                imageViewSetChanges.visibility = ImageView.VISIBLE
            } else {
                imageViewSetChanges.visibility = ImageView.INVISIBLE
            }
        }
//==================================================================================================
        viewModel.collectionUserPhotoLD.observe(this) {
            //Здесь отправляем в адаптер только адреса для отображения. Имя файлов для этого не требуется
            val tempListOfPhotoUri: MutableList<Uri> = mutableListOf()
            for ((key, value) in it) {
                tempListOfPhotoUri.add(value)
            }
            Log.d(TAG, "observeViewModel: вызов адаптера")
            adapter.urlUserPhotoList = tempListOfPhotoUri
        }
//==================================================================================================
//        val drawableUnselectedUserPhotoRes = R.drawable.black_heart_unselected_user_photo
//        val drawableSelectedUserPhotoRes = R.drawable.black_heart_selected_user_photo
//        val unselectedUserPhoto =
//            ContextCompat.getDrawable(this, drawableUnselectedUserPhotoRes)
//        val selectedUserPhoto =
//            ContextCompat.getDrawable(this, drawableSelectedUserPhotoRes)
        //Если при запуске профайла обнаружено, что фото нет
        if (user_photo_Uri.equals("")) {
            Glide.with(this)
                .load(R.drawable.colt_bg_logo)
                .into(imageViewUserPhoto)
        } else {
            Glide.with(this)
                .load(user_photo_Uri)
                .into(imageViewUserPhoto)
        }

        Log.d(TAG, "observeViewModel: user_photo_Uri = $user_photo_Uri")

        viewModel.collectionUserMainPhotoLD.observe(this) {
            Log.d(TAG, "observeViewModel: it = $it")
            if (it.isEmpty()) {
                user_photo_Uri = ""
                Glide.with(this)
                    .load(R.drawable.colt_bg_logo)
                    .into(imageViewUserPhoto)
                myEdit = preferences.edit()
                myEdit.putString("user_photo", user_photo_Uri).commit()
            } else {
                for ((key, value) in it) {
                    user_photo_Uri = value.toString()
                    Glide.with(this)
                        .load(user_photo_Uri)
                        .into(imageViewUserPhoto)
                    myEdit = preferences.edit()
                    myEdit.putString("user_photo", user_photo_Uri).commit()
                }
            }

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
//            imageChooser()
//            showImageDialog()

            cropActivityResultLauncher.launch(null)
        })

        adapter.onDeletePhotoClickListener {
            viewModel.deleteSelectedPhoto(it)
            Log.d(TAG, "setupOnClickListeners: $it")
        }

        adapter.onMakePhotoClickListener({
            viewModel.setUserMainPhoto(it)
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
        imageViewShowPhoto = findViewById(R.id.imageViewShowPhoto)
        imageViewUserPhoto = findViewById(R.id.imageViewUserPhoto)
        progressBar = findViewById(R.id.progressBar)

        adapter = UserPhotoAdapter(localClassName)
        recycleViewUserPhoto = findViewById(R.id.recycleViewUserPhoto)
        recycleViewUserPhoto.adapter = adapter
        recycleViewUserPhoto.layoutManager = GridLayoutManager(this, 2)

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

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
        )
    }
}