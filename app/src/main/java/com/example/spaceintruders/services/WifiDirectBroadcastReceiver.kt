package com.example.spaceintruders.services

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pManager
import androidx.core.app.ActivityCompat

class WifiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val wifiBroadcastListener: WifiBroadcastListener
): BroadcastReceiver() {

//    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
//                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
//                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {

                // The peer list has changed! We should probably do something about
                // that.

                if (context?.let {
                        ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } == PackageManager.PERMISSION_GRANTED
                ) {
                    manager.requestPeers(channel, wifiBroadcastListener.peerListListener)
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Connection state changed! We should probably do something about
                // that.
                val group: WifiP2pGroup? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
                if (group == null || (group.clientList.isEmpty() && group.isGroupOwner || group.owner == null)) {
                    wifiBroadcastListener.notConnected()
                } else {
                    manager.requestConnectionInfo(channel, wifiBroadcastListener.connectionInfoListener)
                }
            }
        }
    }

    interface WifiBroadcastListener {
        fun notConnected()
        val peerListListener : WifiP2pManager.PeerListListener
        val connectionInfoListener : WifiP2pManager.ConnectionInfoListener
    }
}