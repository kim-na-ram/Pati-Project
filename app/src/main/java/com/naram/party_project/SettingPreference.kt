package com.naram.party_project

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth

class SettingPreference : PreferenceFragmentCompat() {

    private var signOutPreference : Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        setting_signout()

    }

    private fun setting_signout() {
        signOutPreference = findPreference("key_sign_out")

        signOutPreference!!.setOnPreferenceClickListener {
            val auth = FirebaseAuth.getInstance()
            auth?.signOut()

            activity?.finish()
            startActivity(Intent(activity, LoginActivity::class.java))

            true
        }
    }

}