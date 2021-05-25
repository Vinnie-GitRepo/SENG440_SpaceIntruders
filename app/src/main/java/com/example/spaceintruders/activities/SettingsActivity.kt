package com.example.spaceintruders.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.apandroid.colorwheel.ColorWheel
import com.example.spaceintruders.R

class SettingsActivity : AppCompatActivity() {

    var username: String =  ""
    var colour: Int = Color.rgb(255, 255, 255)
    var darkmode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.settings, SettingsFragment())
//                .commit()
//        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var usernameField = findViewById<EditText>(R.id.usernameinputtext)
        var darkswitch = findViewById<Switch>(R.id.darkmodeswitch)
        var colourWheel = findViewById<ColorWheel>(R.id.colorWheel)
        var saveSettingButton = findViewById<Button>(R.id.savesettingsbutton)

        loadSettings(findViewById(android.R.id.content))
        usernameField.setText(username)
        darkswitch.isChecked = darkmode
        wheel()

        saveSettingButton.setOnClickListener{
            username = usernameField.text.toString()
            darkmode = darkswitch.isChecked()
            saveSettings(findViewById(android.R.id.content))
        }
    }


    fun saveSettings(view: View) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("name", username)
            putInt("colour", colour)
            putBoolean("darkmode", darkmode)
            commit()
        }
    }

    fun loadSettings(view: View) {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        username = sharedPref.getString("name", "username").orEmpty()
        colour = sharedPref.getInt("colour", Color.rgb(255, 255, 255))
        darkmode = sharedPref.getBoolean("darkmode", false)

    }

    fun wheel() {
        var colourWheel = findViewById<ColorWheel>(R.id.colorWheel)
        colourWheel.rgb = colour
        colourWheel.colorChangeListener = { rgb: Int ->
            colour = colourWheel.rgb
        }
    }


//    class SettingsFragment : PreferenceFragmentCompat() {
//        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//            setPreferencesFromResource(R.xml.root_preferences, rootKey)
//        }
//    }
}