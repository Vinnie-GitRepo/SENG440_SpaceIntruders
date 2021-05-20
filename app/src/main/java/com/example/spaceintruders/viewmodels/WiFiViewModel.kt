package com.example.spaceintruders.viewmodels

import android.Manifest
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spaceintruders.services.WiFiDirectBroadcastReceiver

class WiFiViewModel(application: Application) : AndroidViewModel(application), WiFiDirectBroadcastReceiver.WifiBroadcastListener {
    // Private variables
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel

    private lateinit var receiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    // Observable Variables
    private var _peers: MutableLiveData<MutableList<WifiP2pDevice>> = MutableLiveData(mutableListOf())
    val peers: LiveData<MutableList<WifiP2pDevice>>
        get() = _peers

    private var _connected = MutableLiveData<Boolean>()
    val connected: LiveData<Boolean>
        get() = _connected

    private var _connectionStatus: MutableLiveData<String> = MutableLiveData("")
    val connectionStatus: LiveData<String>
        get() = _connectionStatus


    // Functions
    init {
        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
    }

    fun initialiseManager(manager: WifiP2pManager, channel: WifiP2pManager.Channel) {
        this.manager = manager
        this.channel = channel
        receiver = WiFiDirectBroadcastReceiver(manager, channel, this)
    }

    fun discoverPeers(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _connectionStatus.value = "Discovery Started"
                }

                override fun onFailure(reason: Int) {
                    _connectionStatus.value = "Discovery cannot start"
                }
            })
        }
    }

    override fun notConnected() {
        _connected.value = false
    }


    // Interface values
    override val peerListListener: WifiP2pManager.PeerListListener = WifiP2pManager.PeerListListener {
        if (it != peers) {
            _peers.value!!.clear()
            _peers.value!!.addAll(it.deviceList)
        }
    }

    override val connectionInfoListener: WifiP2pManager.ConnectionInfoListener = WifiP2pManager.ConnectionInfoListener {
        val groupOwnerAddress = it.groupOwnerAddress
        if (it.groupFormed && it.isGroupOwner) {
            _connectionStatus.value = "Group owner"
        } else if (it.groupFormed) {
            _connectionStatus.value = "Client"
        }
    }

}