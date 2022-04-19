package com.naram.party_project.ui.main.setting

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.naram.party_project.R
import com.naram.party_project.data.local.AppDatabase
import com.naram.party_project.ui.signin.SigninActivity

class SettingPreference : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val auth = FirebaseAuth.getInstance()

        auth?.let {
            setting_email(auth)
            setting_signout(auth)
        }


    }

    private fun setting_email(auth : FirebaseAuth) {

        val emailPreference : Preference? = findPreference("key_showEmail")

        emailPreference?.title = auth.currentUser?.email

    }

    private fun setting_signout(auth : FirebaseAuth) {
        val signOutPreference : Preference? = findPreference("key_signOut")

        signOutPreference!!.setOnPreferenceClickListener {

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

            true
        }
    }

}