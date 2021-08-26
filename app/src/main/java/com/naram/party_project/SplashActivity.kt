package com.naram.party_project

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_TIME_OUT : Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val flag : Boolean = checkUserInfo()

        Handler().postDelayed({

            if(flag) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }

            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun checkUserInfo() : Boolean {

        val auth = FirebaseAuth.getInstance()
        val user = auth?.currentUser

        return user == null
    }
}