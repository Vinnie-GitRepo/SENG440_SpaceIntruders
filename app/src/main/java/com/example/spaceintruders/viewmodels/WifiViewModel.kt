package com.example.spaceintruders.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spaceintruders.services.WifiDirectBroadcastReceiver

class WifiViewModel(application: Application) : AndroidViewModel(application), WifiDirectBroadcastReceiver.WifiBroadcastListener {
    // Constants


    // Private variables
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel

    private lateinit var receiver: BroadcastReceiver

    // Observable Variables
    private var _peers: MutableLiveData<MutableList<WifiP2pDevice>> = MutableLiveData(mutableListOf())
    val peers: LiveData<MutableList<WifiP2pDevice>>
        get() = _peers

    private var _connected = MutableLiveData<Int>()
    val connected: LiveData<Int>
        get() = _connected

    // Functions
    init {

    }

    fun initialiseManager(manager: WifiP2pManager, channel: WifiP2pManager.Channel, receiver: WifiDirectBroadcastReceiver) {
        this.manager = manager
        this.channel = channel
        this.receiver = receiver
    }

    fun discoverPeers(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    _connected.value = DISCOVERING
                }

                override fun onFailure(reason: Int) {
                    _connected.value = DISCOVERY_FAILED
                }
            })
        }
    }

    @SuppressLint("MissingPermission")
    fun connect(device: WifiP2pDevice) {
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                _connected.value = CONNECTED
            }

            override fun onFailure(reason: Int) {
                _connected.value = CONNECTION_FAILED
            }
        })
    }

    override fun notConnected() {
        _connected.value = NOT_CONNECTED
    }


    // Interface values
    override val peerListListener: WifiP2pManager.PeerListListener = WifiP2pManager.PeerListListener {
        if (it != peers) {
            _peers.value!!.clear()
            _peers.value!!.addAll(it.deviceList)
            _peers.value = _peers.value

            Log.d("peers", "hello")
        }
    }

    override val connectionInfoListener: WifiP2pManager.ConnectionInfoListener = WifiP2pManager.ConnectionInfoListener {
        val groupOwnerAddress = it.groupOwnerAddress
        if (it.groupFormed && it.isGroupOwner) {
            _connected.value = CONNECTED
        } else if (it.groupFormed) {
            _connected.value = CONNECTED
        }
    }

    companion object {
        const val NOT_CONNECTED = 0
        const val CONNECTED = 1
        const val DISCOVERING = 2
        const val DISCOVERY_FAILED = 3
        const val CONNECTION_FAILED = 4
    }
}