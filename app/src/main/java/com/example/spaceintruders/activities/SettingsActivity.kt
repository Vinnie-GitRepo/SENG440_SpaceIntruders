package com.example.spaceintruders.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.media.Image
import android.os.Bundle
import android.os.SystemClock
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        ///start of block to move to result screen

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

        //end of block to move to result screen
        //also remove button from settings layout

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val notifsOn = sharedPref.getBoolean("notificationswitch", false)

        notificationChannel()
        val notifIntent = Intent(applicationContext, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(applicationContext, 0, it, 0)
        }
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(notifsOn) {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 86400000, notifIntent)
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

    fun notificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.CATEGORY_REMINDER, "Daily Reminder", importance).apply {
            description = "A daily reminder to play Space Intruders"
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }


    class AlarmReceiver : BroadcastReceiver() {

        @SuppressLint("ServiceCast")
        override fun onReceive(context: Context, intent: Intent) {
            val openAppIntent: PendingIntent = Intent(context, SplashScreen::class.java).run {
                PendingIntent.getActivity(context, 0, this, 0)
            }

            val notification = Notification.Builder(context, Notification.CATEGORY_REMINDER).run {
                setSmallIcon(R.drawable.ic_action_name)
                setContentTitle("The Galaxy Needs you!")
                setContentText("Why not play a friendly game of Space Intruders?")
                setContentIntent(openAppIntent)
                setAutoCancel(true)
                build()
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification)
        }
    }





    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }



}