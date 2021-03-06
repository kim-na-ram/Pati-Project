package com.naram.party_project.ui.signup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.ui.base.BaseActivity
import com.naram.party_project.util.Const.Companion.FIREBASE_GAME
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY
import com.naram.party_project.util.Const.Companion.FIREBASE_USER
import com.naram.party_project.databinding.ActivitySignupBinding
import com.naram.party_project.data.local.User
import com.naram.party_project.data.remote.api.RetrofitClient
import com.naram.party_project.data.remote.api.UserAPI
import com.naram.party_project.ui.main.MainActivity
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_GAME_MODE
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_PREFERRED_GENDER_MEN
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_PURPOSE
import com.naram.party_project.util.Const.Companion.FIREBASE_TENDENCY_VOICE
import com.naram.party_project.util.Const.Companion.FIREBASE_USERS
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_EMAIL
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_GENDER
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_NAME
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PASSWORD
import com.naram.party_project.util.Const.Companion.FIREBASE_USER_PICTURE
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class SignupActivity : BaseActivity<ActivitySignupBinding>({
    ActivitySignupBinding.inflate(it)
}) {

    companion object {
        private const val REQUEST_GALLERY = 1000
        private const val REQUEST_SUCCESS = 1001
    }

    private val TAG = "Sign Up"

    private lateinit var ref: DatabaseReference

    private var _userPicture: String? = null
    private var _userBitPicture: String? = null
    private lateinit var _userEmail: String
    private lateinit var _userPassword: String
    private lateinit var _userNickName: String
    private lateinit var _userGender: String

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

    }

    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }

    private fun initViews() {

        binding.btnEnterUserPic.setOnClickListener {
            getUserPermission()
        }

        binding.btnUserSignup.setOnClickListener {

            when (editTextNullCheck()) {
                true -> {
                    val auth = FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(
                        binding.etSignupUserEmail.toStringFromText(),
                        binding.etSignupUserPW.toStringFromText()
                    )
                        .addOnCompleteListener(this) { _ ->
                            if(bitmap == null) saveUserInfoToDB()
                            else {

                                uploadImageToCloud(bitmap!!)
                            }
                            Toast.makeText(this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                        }
                }
                else -> {
                    Toast.makeText(this, "?????????, ?????????, ????????????, ????????? ??? ???????????? ?????????.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    private fun getUserPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateGallery()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }
            else -> {
                // TODO ?????? ??????
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("?????? ??????")
            .setMessage("????????? ????????? ???????????? ?????? ????????? ???????????????.")
            .setPositiveButton("??????") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            }
            .setNegativeButton("??????") { _, _ -> }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateGallery()
                } else {
                    Toast.makeText(this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }

    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_SUCCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_SUCCESS -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {
                    binding.ivSignupUserPic.setImageURI(selectedImageUri)
                    bitmap = resize(this, selectedImageUri, 400)
                } else {
                    Toast.makeText(this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resize(context: Context, uri: Uri, resize: Int): Bitmap {
        var resizeBitmap: Bitmap? = null

        val options = BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

            var width = options.outWidth
            var height = options.outHeight
            var samplesize = 1;

            while (true) {
                if (width / 2 < resize || height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            val bitmap = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri),
                null,
                options
            )
            resizeBitmap = bitmap;

        } catch (e: FileNotFoundException) {
            e.printStackTrace();
        }

        return resizeBitmap!!
    }

    private fun editTextNullCheck(): Boolean {

        _userNickName = binding.etSignupUserName.toStringFromText()
        _userEmail = binding.etSignupUserEmail.toStringFromText()
        _userPassword = binding.etSignupUserPW.toStringFromText()

        when (binding.radioUserGender.checkedRadioButtonId) {
            binding.radiobuttonUserFemale.id -> _userGender = "F"
            binding.radiobuttonUserMale.id -> _userGender = "M"
        }

        if (_userNickName.isNotEmpty() && _userEmail.isNotEmpty() && _userPassword.isNotEmpty()) {
            if (!_userGender.isNullOrEmpty())
                return true
        }

        return false

    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imgArray = stream.toByteArray()

        _userBitPicture = Base64.encodeToString(imgArray, Base64.DEFAULT)

    }

    private fun uploadImageToCloud(bitmap: Bitmap) {
        val storage = Firebase.storage
        var storageRef = storage.reference
        var imagesRef = storageRef.child(_userEmail).child("profile.jpg")

//        var bitmap: Bitmap
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
//        } else {
//            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
//        }
//
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val data = baos.toByteArray()

        var uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this@SignupActivity, "?????? ???????????? ??????????????????.", Toast.LENGTH_SHORT)
                .show()
        }.addOnSuccessListener { taskSnapshot ->
//            _userPicture = imagesRef.toString()
            _userPicture = "$_userEmail/profile.jpg"
            saveUserInfoToDB()
        }

    }

    private fun saveUserInfoToDB() {
//        runBlocking {
//            insertData(
//                _userEmail,
//                _userPassword,
//                _userNickName,
//                _userGender,
//                _userPicture
//            )
//        }

        runBlocking {
            insertFirebase(
                FirebaseAuth.getInstance().currentUser!!.uid,
                _userEmail,
                _userPassword,
                _userNickName,
                _userGender,
                _userPicture
            )
        }

        insertRoom(
            _userEmail,
            _userNickName,
            _userGender,
            _userPicture
        )

    }

    private fun insertFirebase(uid : String, email : String, password : String, name : String, gender : String, picture: String?) {
        ref = FirebaseDatabase.getInstance().reference
        val mDatabaseReference = ref.database.getReference("$FIREBASE_USERS/$uid")

        mDatabaseReference.child(FIREBASE_USER).child(FIREBASE_USER_EMAIL).setValue(email).addOnCompleteListener {
            Log.d(TAG, "???????????? ??????")
        }.addOnFailureListener {
            Log.d(TAG, "???????????? ?????? : ${it.localizedMessage}")
        }
        mDatabaseReference.child(FIREBASE_USER).child(FIREBASE_USER_PASSWORD).setValue(password)
        mDatabaseReference.child(FIREBASE_USER).child(FIREBASE_USER_NAME).setValue(name)
        mDatabaseReference.child(FIREBASE_USER).child(FIREBASE_USER_GENDER).setValue(gender)
        picture?.let {
            mDatabaseReference.child(FIREBASE_USER).child(FIREBASE_USER_PICTURE).setValue(picture)
        }

//        val mTendencyDatabaseReference = ref.database.getReference("$FIREBASE_TENDENCY")

        mDatabaseReference.child(FIREBASE_TENDENCY).child(FIREBASE_TENDENCY_PURPOSE).setValue("0")
        mDatabaseReference.child(FIREBASE_TENDENCY).child(FIREBASE_TENDENCY_VOICE).setValue("0")
        mDatabaseReference.child(FIREBASE_TENDENCY).child(FIREBASE_TENDENCY_PREFERRED_GENDER_MEN).setValue("1")
        mDatabaseReference.child(FIREBASE_TENDENCY).child(FIREBASE_TENDENCY_PREFERRED_GENDER_WOMEN).setValue("1")
        mDatabaseReference.child(FIREBASE_TENDENCY).child(FIREBASE_TENDENCY_GAME_MODE).setValue("1")

//        val mGameDatabaseReference = ref.database.getReference("$FIREBASE_GAME")

        for(i in 0..9) {
            mDatabaseReference.child(FIREBASE_GAME).child("game$i").setValue("0", i)
        }

    }

    private fun insertData(vararg params: String?) {
        CoroutineScope(Dispatchers.Default).launch {
            val email: String? = params[0]
            val password: String? = params[1]
            val name: String? = params[2]
            val gender: String? = params[3]
            val picture: String? = params[4]

            // Use Retrofit
            val retrofit = RetrofitClient.getInstance()

            val server = retrofit.create(UserAPI::class.java)
            val call : Call<String> = server.putUser(email!!, password!!, name!!, gender!!, picture)
            call.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG,"?????? : "+t.localizedMessage)
                    Toast.makeText(this@SignupActivity, "????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "?????? : ${response.body().toString()}")
                    } else {
                        Log.d(TAG, "?????? : ${response.errorBody().toString()}")
                    }
                }
            })

        }

    }

    private fun insertRoom(email: String, user_name: String, gender: String, picture: String?) {
        val thread = Thread(Runnable {
            db.userDAO().insertUserInfo(
                User(
                    email,
                    picture,
                    user_name,
                    null,
                    gender,
                    null
                )
            )

            db.tendencyDAO().insertUserTendencyInfo(email)

            db.gameDAO().insertUserGameInfo(email)

        })

        thread.start()

        finish()
        startActivity(Intent(this@SignupActivity, MainActivity::class.java))
    }

    fun EditText.toStringFromText(): String {
        return this.text.toString()
    }

}