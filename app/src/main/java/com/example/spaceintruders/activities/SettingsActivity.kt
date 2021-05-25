package com.example.spaceintruders.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.spaceintruders.R

class SettingsActivity : AppCompatActivity() {

    var username = ""
    var colour = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun saveSettings(){
        val sharedPref = activity?.getSharedPreferences("profile_settings", Context.MODE_PRIVATE)?: return
        with(sharedPref.edit()) {
            putString("name", username)
            putString("colour", colour)
            commit()
        }
    }

    fun loadSettings(){
        val sharedPref = activity?.getSharedPreferences("profile_settings", Context.MODE_PRIVATE)?: return
        username = sharedPref.getString("name", "username")
        colour = sharedPref.getString("colour", "white")
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}