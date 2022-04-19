package com.naram.party_project.ui.main.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.ui.signin.SigninActivity
import com.naram.party_project.ui.base.BaseFragment
import com.naram.party_project.data.local.AppDatabase
import com.naram.party_project.databinding.FragmentSettingBinding
import com.naram.party_project.ui.modify.ModifyProfileActivity

class SettingFragment : BaseFragment<FragmentSettingBinding>() {

    override fun getFragmentBinding() = FragmentSettingBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        val auth = FirebaseAuth.getInstance()

        binding.tvEmail.text = auth.currentUser?.email

        binding.llSignOut.setOnClickListener {
            val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java,
                "userDB"
            ).build()

            Thread(kotlinx.coroutines.Runnable {
                db.userDAO().deleteAll()
            }).start()

            auth.signOut()

            activity?.finish()
            startActivity(Intent(activity, SigninActivity::class.java))

        }

        binding.llModifyProfile.setOnClickListener {
            startActivity(Intent(requireContext(), ModifyProfileActivity::class.java))
        }
    }
}