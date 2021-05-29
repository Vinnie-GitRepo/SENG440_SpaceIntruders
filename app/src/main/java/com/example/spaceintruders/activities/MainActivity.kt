package com.example.spaceintruders.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
//import android.support.wearable.activity.WearableActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.spaceintruders.R
import com.example.spaceintruders.services.NearbyCommunication
import com.example.spaceintruders.services.WifiDirectBroadcastReceiver
import com.example.spaceintruders.viewmodels.WifiViewModel

/**
 * Fragments for main menu pairing and the game play will branch off this.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var nearbyCommunication: NearbyCommunication
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel

//    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val model: NearbyCommunication by viewModels()
        nearbyCommunication = model
        nearbyCommunication.setConnectionObserver(this)


        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

//        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
//        channel = manager.initialize(this, Looper.getMainLooper(), null)
//        receiver = WifiDirectBroadcastReceiver(manager, channel, model)
//
//        wifiViewModel = model
//        wifiViewModel.initialiseManager(manager, channel, receiver as WifiDirectBroadcastReceiver)

    }

    override fun onResume() {
        super.onResume()
//        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(receiver)
    }


}