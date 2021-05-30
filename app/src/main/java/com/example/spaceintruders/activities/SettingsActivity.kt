package com.example.spaceintruders.activities

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.apandroid.colorwheel.ColorWheel
import com.example.spaceintruders.R

class SettingsActivity : AppCompatActivity() {

    var colour: Int = Color.rgb(255, 255, 255)

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

        loadSettings(findViewById(android.R.id.content))

        var shipImage = findViewById<ImageView>(R.id.shipImage)
        shipImage.setColorFilter(colour, PorterDuff.Mode.SRC_ATOP)

        var colourWheel = findViewById<ColorWheel>(R.id.colorWheel)
        colourWheel.rgb = colour
        colourWheel.colorChangeListener = { rgb: Int ->
            colour = colourWheel.rgb
            shipImage.setColorFilter(colour, PorterDuff.Mode.SRC_ATOP)
        }


//        var saveSettingButton = findViewById<Button>(R.id.savesettingsbutton)
//        saveSettingButton.setOnClickListener{
//            saveSettings(findViewById(android.R.id.content))
//        }

    }

    override fun onStop() {
        super.onStop()
        saveSettings(findViewById(android.R.id.content))
    }

    fun saveSettings(view: View) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPref.edit()) {
            putInt("colour", colour)
            commit()
        }
    }

    fun loadSettings(view: View) {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        colour = sharedPref.getInt("colour", Color.rgb(255, 255, 255))
    }



    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }


}