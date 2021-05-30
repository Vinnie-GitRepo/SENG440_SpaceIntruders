package com.example.spaceintruders.activities

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.apandroid.colorwheel.ColorWheel
import com.example.spaceintruders.R

class ColorPickerActivity : AppCompatActivity() {
    var colour: Int = Color.rgb(255, 255, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.colour_picker_layout)
        loadSettings(findViewById(android.R.id.content))

        var shipImage = findViewById<ImageView>(R.id.shipImage)
        shipImage.setColorFilter(colour, PorterDuff.Mode.SRC_ATOP)

        var colourWheel = findViewById<ColorWheel>(R.id.colorWheel)
        colourWheel.rgb = colour
        colourWheel.colorChangeListener = { rgb: Int ->
            colour = colourWheel.rgb
            shipImage.setColorFilter(colour, PorterDuff.Mode.SRC_ATOP)
        }
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
}