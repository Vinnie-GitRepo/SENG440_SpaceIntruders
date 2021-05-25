package com.example.spaceintruders.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.spaceintruders.R

class SettingsActivity : AppCompatActivity() {

    var username: String =  ""
    var colour: String = "#FFFFFF"

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


    fun saveSettings(view: View) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("name", username)
            putString("colour", colour)
            commit()
        }
    }

    fun loadSettings(view: View) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        username = sharedPref.getString("name", "username").orEmpty()
        colour = sharedPref.getString("colour", "#FFFFFF").orEmpty()

    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}