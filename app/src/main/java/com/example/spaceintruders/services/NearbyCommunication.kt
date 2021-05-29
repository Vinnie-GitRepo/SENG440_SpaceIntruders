package com.example.spaceintruders.services

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.collection.SimpleArrayMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.spaceintruders.R
import com.example.spaceintruders.gameentities.Bullet
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
        private val incomingPayloads: SimpleArrayMap<Long, Payload> = SimpleArrayMap()
        override fun onPayloadReceived(endpoint: String, payload: Payload) {
            Log.d("PayloadListener", "Payload Received")
            payload.asBytes()?.let { bytes ->
                _instruction.value = String(bytes)
            }
        }
        override fun onPayloadTransferUpdate(endpoint: String, update: PayloadTransferUpdate) {
        }
    }

    /**##### Functions #####**/
    fun sendBullet(context: Context, bullet: Bullet) {
        val message = "bullet${bullet.positionX}"
        sendMessage(context, message)
    }

    fun sendYouScored(context: Context) {
        val message = "scored"
        sendMessage(context, message)
    }

    fun sendOpponentVictoryCondition(context: Context) {
        val message = "youwon"
        sendMessage(context, message)
    }

    fun resetInstruction() {
        _instruction.value = ""
    }

    private fun sendMessage(context: Context, message: String) {
        hostEndpoint?.let {
            Nearby.getConnectionsClient(context).sendPayload(it, Payload.fromBytes(message.toByteArray()))
        }
    }

    /**##### Discovery and Pairing #####**/
    fun disconnect(context: Context) {
        _connected.value = NOT_CONNECTED
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }

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
        _connected.value = CONNECTING
        val callback = createDiscoverListener()
        _peers.value = mutableListOf()

        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context).startDiscovery("spaceIntruders", callback, options).addOnSuccessListener {
            Log.d("Discovery succeeded", "Yay")
            _connected.value = DISCOVERING
        }.addOnFailureListener {
            Log.d("Discovery Failed", it.toString())
        }
    }

    fun joinHost(context: Context, endpoint: Endpoint) {
        val oldValue = _connected.value
        _connected.value = CONNECTING
        Nearby.getConnectionsClient(context)
            .requestConnection("CLIENT", endpoint.id, connectionListener)
            .addOnSuccessListener {
                Toast.makeText(context, context.getString(R.string.peer_connected), Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, context.getString(R.string.rejected), Toast.LENGTH_LONG).show()
                _connected.value = oldValue
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

    /**##### Initialisation ####**/
    fun setConnectionObserver(context: Context) {
        connectionListener = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(id: String, info: ConnectionInfo) {
                Log.d("Connection", "Connection initiated")
                Nearby.getConnectionsClient(context).acceptConnection(id, payloadListener)
            }

            override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        Nearby.getConnectionsClient(context).stopDiscovery()
                        Nearby.getConnectionsClient(context).stopAdvertising()
                        _peers.value = mutableListOf()
                        hostEndpoint = endpoint
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
                Toast.makeText(context, context.getString(R.string.unexpectedDisconnect), Toast.LENGTH_LONG).show()
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
        const val DISCONNECTED = 6
    }
}