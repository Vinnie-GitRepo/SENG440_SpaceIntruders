package com.example.spaceintruders.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.viewModels
//import android.support.wearable.activity.WearableActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.spaceintruders.R
import com.example.spaceintruders.services.WifiDirectBroadcastReceiver
import com.example.spaceintruders.viewmodels.WifiViewModel

/**
 * Fragments for main menu pairing and the game play will branch off this.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var wifiViewModel: WifiViewModel
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel

    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val model: WifiViewModel by viewModels()

        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, Looper.getMainLooper(), null)
        receiver = WifiDirectBroadcastReceiver(manager, channel, model)

        wifiViewModel = model
        wifiViewModel.initialiseManager(manager, channel, receiver as WifiDirectBroadcastReceiver)

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}