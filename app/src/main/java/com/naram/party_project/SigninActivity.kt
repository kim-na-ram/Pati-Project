package com.naram.party_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private var firebaseAuth : FirebaseAuth? = null

    private val et_signinUserEmail : EditText by lazy {
        findViewById(R.id.et_signinUserEmail)
    }

    private val et_signinUserPW : EditText by lazy {
        findViewById(R.id.et_signinUserPW)
    }

    private val btn_userSignin : Button by lazy {
        findViewById(R.id.btn_userSignin)
    }

    private val tv_userSignup : TextView by lazy {
        findViewById(R.id.tv_userSignup)
    }

    private val tv_findAccount : TextView by lazy {
        findViewById(R.id.tv_findAccount)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        initViews()

    }

    private fun initViews() {

        btn_userSignin.setOnClickListener {
            val userEmail = et_signinUserEmail.text.toString()
            val userPW = et_signinUserPW.text.toString()

            if(userEmail.isEmpty() || userPW.isEmpty()) return@setOnClickListener
            else {
                checkFirebaseAuth(userEmail, userPW)

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ID", userEmail)
                finish()
                startActivity(intent)
            }

        }

        tv_findAccount.setOnClickListener {
            // TODO firebase 비밀번호 찾기
        }

        tv_userSignup.setOnClickListener {
            finish()
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }

    private fun checkFirebaseAuth(userID : String, userPW : String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth!!.signInWithEmailAndPassword(userID, userPW).addOnCompleteListener {
            if(it.isSuccessful) {
                return@addOnCompleteListener
            } else {
                Toast.makeText(this, "이메일 혹은 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}