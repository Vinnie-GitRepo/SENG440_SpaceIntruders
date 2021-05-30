package com.example.spaceintruders.activities

import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.spaceintruders.R
import com.example.spaceintruders.viewmodels.NearbyCommunication

/**
 * Fragments for main menu pairing and the game play will branch off this.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var nearbyCommunication: NearbyCommunication

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
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}