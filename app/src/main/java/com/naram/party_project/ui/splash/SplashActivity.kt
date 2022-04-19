package com.naram.party_project.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.R
import com.naram.party_project.ui.main.MainActivity
import com.naram.party_project.ui.signin.SigninActivity

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_TIME_OUT : Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val flag : Boolean = checkUserInfo()

        Handler().postDelayed({

            if(!flag) {
                startActivity(Intent(this, SigninActivity::class.java))
            } else {
                // when) Database Migration
//                FirebaseAuth.getInstance().signOut()
//                startActivity(Intent(this, SigninActivity::class.java))

                // Origin Code
                startActivity(Intent(this, MainActivity::class.java))
            }

            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun checkUserInfo() : Boolean {

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        return user != null
    }
}