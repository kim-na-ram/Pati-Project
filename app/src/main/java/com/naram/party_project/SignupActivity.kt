package com.naram.party_project

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class SignupActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GALLERY = 1000
        private const val REQUEST_SUCCESS = 1001
    }

    private val IP_ADDRESS = ""
    private val INSERT_URL = "http://$IP_ADDRESS/userInsert.php"

    private lateinit var _userPicture: String
    private lateinit var _userEmail: String
    private lateinit var _userPassword: String
    private lateinit var _userNickName: String
    private lateinit var _userGender: String

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
                        et_signupUserEmail.TexttoString(),
                        et_signupUserPW.TexttoString()
                    )
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {

                                // TODO 정보들을 user_tb에 저장
                                val insert = InsertData()
                                insert.execute(
                                    INSERT_URL,
                                    _userEmail,
                                    _userPassword,
                                    _userNickName,
                                    _userGender,
                                    _userPicture
                                )

                                startActivity(Intent(this, SigninActivity::class.java))
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
                    _userPicture = selectedImageUri?.toString()
                    iv_signupUserPic.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun editTextNullCheck(): Boolean {

        _userNickName = et_signupUserName.TexttoString()
        _userEmail = et_signupUserEmail.TexttoString()
        _userPassword = et_signupUserPW.TexttoString()

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

    /*Insert Data in mysql*/
    private class InsertData : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val email: String? = params[1]
            val password: String? = params[2]
            val user_name: String? = params[3]
            val gender: String? = params[4]
            val picture: String? = params[5]

            val postParameters = "email=$email&password=$password&user_name=$user_name&gender=$gender&picture=$picture"

            try {
                val url = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.connect()

                val outputStream: OutputStream = httpURLConnection.outputStream
                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                outputStream.flush()
                outputStream.close()

                val responseStatusCode: Int = httpURLConnection.responseCode

                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    httpURLConnection.inputStream
                } else {
                    httpURLConnection.errorStream
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)

                val sb = StringBuilder()
                var line: String? = null

                while (bufferedReader.readLine().also({ line = it }) != null) {
                    sb.append(line)
                }

                bufferedReader.close();

                return sb.toString();

            } catch (e: Exception) {
                return "Error" + e.message
            }

        }

    }

    fun EditText.TexttoString(): String {
        return this.text.toString()
    }

}