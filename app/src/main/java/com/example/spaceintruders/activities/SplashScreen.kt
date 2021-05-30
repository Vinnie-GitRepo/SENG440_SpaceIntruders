package com.example.spaceintruders.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import androidx.preference.PreferenceManager
import com.example.spaceintruders.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

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

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) //milliseconds.


    }

    fun notificationChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
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
                setContentTitle(context.getString(R.string.notification_title))
                setContentText(context.getString(R.string.notification_text))
                setContentIntent(openAppIntent)
                setAutoCancel(true)
                build()
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notification)
        }
    }
}