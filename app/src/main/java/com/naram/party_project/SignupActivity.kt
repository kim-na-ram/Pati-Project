package com.naram.party_project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.naram.party_project.PatiConstClass.Companion.IP_ADDRESS
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class SignupActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GALLERY = 1000
        private const val REQUEST_SUCCESS = 1001
    }

    private val INSERT_URL = "http://$IP_ADDRESS/userInsert.php"
    private val LOG_TAG = "SignupActivity"

    private var _userPicture: String? = null
    private lateinit var _userEmail: String
    private lateinit var _userPassword: String
    private lateinit var _userNickName: String
    private lateinit var _userGender: String

    private lateinit var bitmap: Bitmap

    private val iv_signupUserPic: ImageView by lazy {
        findViewById(R.id.iv_signupUserPic)
    }

    private val btn_enterUserPic: Button by lazy {
        findViewById(R.id.btn_enterUserPic)
    }

    private val et_signupUserName: EditText by lazy {
        findViewById(R.id.et_signupUserName)
    }

    private val radio_userGender: RadioGroup by lazy {
        findViewById(R.id.radio_userGender)
    }

    private val radiobutton_userFemale: RadioButton by lazy {
        findViewById(R.id.radiobutton_userFemale)
    }

    private val radiobutton_userMale: RadioButton by lazy {
        findViewById(R.id.radiobutton_userMale)
    }

    private val et_signupUserEmail: EditText by lazy {
        findViewById(R.id.et_signupUserEmail)
    }

    private val et_signupUserPW: EditText by lazy {
        findViewById(R.id.et_signupUserPW)
    }

    private val btn_userSignup: Button by lazy {
        findViewById(R.id.btn_userSignup)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initViews()

    }

    private fun initViews() {

        btn_enterUserPic.setOnClickListener {
            getUserPermission()
        }

        btn_userSignup.setOnClickListener {
            val flag = editTextNullCheck()

            when (flag) {
                true -> {
                    val auth = FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(
                        et_signupUserEmail.toStringFromText(),
                        et_signupUserPW.toStringFromText()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                if (bitmap != null) uploadImageToCloud(bitmap)
                                else saveUserInfoToDB()

                                finish()
                                startActivity(Intent(this, MakemyprofileActivity::class.java))
//                                startActivity(Intent(this, MainActivity::class.java))
                            } else {
                                Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else -> {
                    Toast.makeText(this, "닉네임, 이메일, 비밀번호, 성별은 꼭 입력해야 합니다.", Toast.LENGTH_SHORT)
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
                // TODO 권한 요청
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한 요청")
            .setMessage("앱에서 사진을 불러오기 위해 권한이 필요합니다.")
            .setPositiveButton("동의", { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY
                )
            })
            .setNegativeButton("취소", { _, _ -> })
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
                    Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
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
                    iv_signupUserPic.setImageURI(selectedImageUri)
                    bitmap = resize(this, selectedImageUri, 400)
                } else {
                    Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
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

        _userNickName = et_signupUserName.toStringFromText()
        _userEmail = et_signupUserEmail.toStringFromText()
        _userPassword = et_signupUserPW.toStringFromText()

        when (radio_userGender.checkedRadioButtonId) {
            radiobutton_userFemale.id -> _userGender = "F"
            radiobutton_userMale.id -> _userGender = "M"
        }

        if (_userNickName.isNotEmpty() && _userEmail.isNotEmpty() && _userPassword.isNotEmpty()) {
            if (!_userGender.isNullOrEmpty())
                return true
        }

        return false

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
            Toast.makeText(this@SignupActivity, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT)
                .show()
        }.addOnSuccessListener { taskSnapshot ->
//            _userPicture = imagesRef.toString()
            _userPicture = "$_userEmail/profile.jpg"
            saveUserInfoToDB()
        }

    }

    private fun saveUserInfoToDB() {
        runBlocking {
            insertData(
                INSERT_URL,
                _userEmail,
                _userPassword,
                _userNickName,
                _userGender,
                _userPicture
            )
        }
    }

    private fun insertData(vararg params: String?) {
        CoroutineScope(Dispatchers.Default).launch {
            val serverURL: String? = params[0]
            val email: String? = params[1]
            val password: String? = params[2]
            val user_name: String? = params[3]
            val gender: String? = params[4]
            val picture: String? = params[5]

            val queue = Volley.newRequestQueue(this@SignupActivity)

            val requestBody = "email=$email&password=$password&user_name=$user_name&gender=$gender&picture=$picture"
            val stringReq : StringRequest =
                object : StringRequest(Method.POST, serverURL,
                    Response.Listener { response ->
                        // response
                        var strResp = response.toString()
                        Log.d("Coroutines-Log", strResp)
                    },
                    Response.ErrorListener { error ->
                        Log.d("Coroutines-Log", "error => $error")
                    }
                ){
                    override fun getBody(): ByteArray {
                        return requestBody.toByteArray(Charset.defaultCharset())
                    }
                }
            queue.add(stringReq)

//            try {
//                val url = URL(serverURL)
//                val httpURLConnection: HttpURLConnection =
//                    url.openConnection() as HttpURLConnection
//
//                httpURLConnection.readTimeout = 5000
//                httpURLConnection.connectTimeout = 5000
//                httpURLConnection.requestMethod = "POST"
//                httpURLConnection.connect()
//
//                val outputStream: OutputStream = httpURLConnection.outputStream
//                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
//                outputStream.flush()
//                outputStream.close()
//
//                val responseStatusCode: Int = httpURLConnection.responseCode
//
//                val inputStream: InputStream
//                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
//                    httpURLConnection.inputStream
//                } else {
//                    httpURLConnection.errorStream
//                }
//
//                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
//                val bufferedReader = BufferedReader(inputStreamReader)
//
//                val sb = StringBuilder()
//                var line: String? = null
//
//                while (bufferedReader.readLine().also({ line = it }) != null) {
//                    sb.append(line)
//                }
//
//                bufferedReader.close()
//
//                Log.d("Coroutines-Log", sb.toString())
//            } catch (e: Exception) {
//                Log.d("Coroutines-Log", "Error" + e.message)
//                Toast.makeText(this@SignupActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            }

        }

    }

    fun EditText.toStringFromText(): String {
        return this.text.toString()
    }

}