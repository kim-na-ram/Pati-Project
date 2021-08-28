package com.naram.party_project

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_GALLERY = 1000
        private const val REQUEST_SUCCESS = 1001
    }

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
                    auth.createUserWithEmailAndPassword(et_signupUserEmail.TexttoString(), et_signupUserPW.TexttoString())
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful) {
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

        val userNickName = et_signupUserName.TexttoString()
        val userEmail = et_signupUserEmail.TexttoString()
        val userPassword = et_signupUserPW.TexttoString()
        var userGender : String? = null

        when (radio_userGender.checkedRadioButtonId) {
            radiobutton_userFemale.id -> userGender = "female"
            radiobutton_userMale.id -> userGender = "male"
        }

        if (userNickName.isNotEmpty() && userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
            if (!userGender.isNullOrEmpty())
                return true
        }

        return false

    }

    fun EditText.TexttoString(): String {
        return this.text.toString()
    }

}