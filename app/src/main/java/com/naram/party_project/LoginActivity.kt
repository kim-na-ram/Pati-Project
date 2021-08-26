package com.naram.party_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private var firebaseAuth : FirebaseAuth? = null

    private val et_loginUserID : EditText by lazy {
        findViewById(R.id.et_loginUserID)
    }

    private val et_loginUserPW : EditText by lazy {
        findViewById(R.id.et_loginUserPW)
    }

    private val btn_loginUserAccount : Button by lazy {
        findViewById(R.id.btn_userSignin)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()

    }

    private fun initViews() {

        btn_loginUserAccount.setOnClickListener {
            val userID = et_loginUserID.text.toString()
            val userPW = et_loginUserPW.text.toString()

            Log.d("LoginActivity", "$userID, $userPW")

            if(userID.isEmpty() || userPW.isEmpty()) return@setOnClickListener
            else {
                checkFirebaseAuth(userID, userPW)

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("ID", et_loginUserID.text.toString())
                finish()
                startActivity(intent)
            }

        }
    }

    private fun checkFirebaseAuth(userID : String, userPW : String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth!!.signInWithEmailAndPassword(userID, userPW).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this, "$userID is success", Toast.LENGTH_SHORT).show()
                return@addOnCompleteListener
            } else {
                Toast.makeText(this, "$userID is failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}