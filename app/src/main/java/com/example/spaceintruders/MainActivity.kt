package com.example.spaceintruders

import android.os.Bundle
//import android.support.wearable.activity.WearableActivity
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
//        setAmbientEnabled()
    }
}