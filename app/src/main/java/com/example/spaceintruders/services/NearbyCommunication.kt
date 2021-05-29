package com.example.spaceintruders.services

import android.Manifest
import android.app.Application
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceintruders.fragments.pairingfragment.WifiPeersRecyclerViewAdapter
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyCommunication(application: Application) : AndroidViewModel(application) {
    /**##### Observable variables #####**/
    private var _instruction: MutableLiveData<String> = MutableLiveData()
    val instruction: LiveData<String>
        get() = _instruction

    private var _connected = MutableLiveData<Int>()
    val connected: LiveData<Int>
        get() = _connected

    private var _peers: MutableLiveData<MutableList<Endpoint>> = MutableLiveData(mutableListOf())
    val peers: LiveData<MutableList<Endpoint>>
        get() = _peers

    /**##### Variables #####**/
    private var hostEndpoint: String? = null

    /**##### Listeners #####**/
    private lateinit var connectionListener: ConnectionLifecycleCallback

    private val payloadListener = object : PayloadCallback() {
        override fun onPayloadReceived(endpoint: String, payload: Payload) {

        }
        override fun onPayloadTransferUpdate(endpoint: String, update: PayloadTransferUpdate) {}
    }

    /**##### Functions #####**/
    fun advertise(context: Context) {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context).startAdvertising(android.os.Build.MODEL, "spaceIntruders", connectionListener, options)
            .addOnSuccessListener {
                Log.d("Success", "Success")
            }.addOnFailureListener {
                Log.d("advertise", it.toString())
            }
    }

    fun discover(context: Context) {
        val callback = createDiscoverListener()

        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context).startDiscovery("spaceIntruders", callback, options).addOnSuccessListener {
            Log.d("Discovery succeeded", "Yay")
            _connected.value = DISCOVERING
        }.addOnFailureListener {
            Log.d("Discovery Failed", it.toString())
            _connected.value = DISCOVERY_FAILED
        }
    }

    fun joinHost(context: Context, endpoint: Endpoint) {
        Nearby.getConnectionsClient(context)
            .requestConnection("CLIENT", endpoint.id, connectionListener)
            .addOnSuccessListener {
                Toast.makeText(context, "Connected to ${endpoint.name}.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Rejected by ${endpoint.name}.", Toast.LENGTH_LONG).show()
            }
    }

    fun createDiscoverListener(): EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                _peers.value!!.add(Endpoint(id, info))
                _peers.value = _peers.value
            }

            override fun onEndpointLost(p0: String) {
                _peers.value!!.removeIf { it.id == p0 }
                _peers.value = _peers.value
            }
        }
    }

    fun setConnectionObserver(context: Context) {
        connectionListener = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(id: String, info: ConnectionInfo) {
                Nearby.getConnectionsClient(context).acceptConnection(id, payloadListener)
            }

            override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        _connected.value = CONNECTED
                    }
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                        _connected.value = CONNECTION_FAILED
                    }
                    ConnectionsStatusCodes.STATUS_ERROR -> {
                        _connected.value = CONNECTION_FAILED
                    }
                }
            }

            override fun onDisconnected(endpoint: String) {
                _connected.value = NOT_CONNECTED
            }
        }
    }

    /**
     * Constant values for connection status'.
     */
    companion object {
        const val NOT_CONNECTED = 0
        const val CONNECTED = 1
        const val DISCOVERING = 2
        const val DISCOVERY_FAILED = 3
        const val CONNECTION_FAILED = 4
        const val CONNECTING = 5
    }
}