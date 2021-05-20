package com.example.spaceintruders.activities

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
//import android.support.wearable.activity.WearableActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spaceintruders.R
import com.example.spaceintruders.viewmodels.WiFiViewModel

/**
 * Fragments for main menu pairing and the game play will branch off this.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var wifiViewModel: WiFiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel = manager.initialize(this, Looper.getMainLooper(), null)
        val model: WiFiViewModel by viewModels()
        wifiViewModel = model
        wifiViewModel.initialiseManager(manager, channel)

    }
}