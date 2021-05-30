package com.example.spaceintruders.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.spaceintruders.R


class SettingsActivity : AppCompatActivity() {
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




        var opponent = "Vinnie"
        var result = "hard-won victory and"
        var score = 20000


        var shareButton = findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener{
            var shareText = "I just battled against $opponent in an intense match of Space Intruders! I had a $result got a score of $score!"

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }



//        var saveSettingButton = findViewById<Button>(R.id.savesettingsbutton)
//        saveSettingButton.setOnClickListener{
//            saveSettings(findViewById(android.R.id.content))
//        }

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}