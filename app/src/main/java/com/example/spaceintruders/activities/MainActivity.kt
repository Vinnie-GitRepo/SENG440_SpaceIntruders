package com.example.spaceintruders.activities

import android.os.Bundle
//import android.support.wearable.activity.WearableActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.spaceintruders.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
//        setAmbientEnabled()
    }
}