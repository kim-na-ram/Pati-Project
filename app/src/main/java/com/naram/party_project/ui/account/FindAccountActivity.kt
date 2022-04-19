package com.naram.party_project.ui.account

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.naram.party_project.ui.base.BaseActivity
import com.naram.party_project.databinding.ActivityFindaccountBinding

class FindAccountActivity : BaseActivity<ActivityFindaccountBinding>({
    ActivityFindaccountBinding.inflate(it)
}) {

    private val TAG : String = "Find Account"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()

    }

    private fun initViews() {

        setSupportActionBar(binding.layoutTop.toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayShowCustomEnabled(true)

        binding.layoutTop.tvTextToolbar.text = "비밀번호 찾기"
        binding.layoutTop.tvTextToolbar.textSize = 23F

        binding.btnSendEmailToRestPassword.setOnClickListener {
            if(binding.etUserEmail.text != null) {
                val email = binding.etUserEmail.text.toString()

                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            AlertDialog.Builder(this)
                                .setMessage("메일을 확인해주세요.")
                        }
                    }
            }
        }
    }
}