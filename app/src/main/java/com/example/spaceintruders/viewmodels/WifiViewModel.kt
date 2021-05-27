package com.example.spaceintruders.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spaceintruders.gameentities.Bullet
import com.example.spaceintruders.services.WifiDirectBroadcastReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class WifiViewModel(application: Application) : AndroidViewModel(application), WifiDirectBroadcastReceiver.WifiBroadcastListener {
    // Private variables
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel

    private lateinit var socket: Socket
    private lateinit var receiver: BroadcastReceiver

    private lateinit var server: Server
    private lateinit var client: Client
    private var groupOwner: Boolean = false

    // Observable Variables
    private var _instruction: MutableLiveData<String> = MutableLiveData()
    val instruction: LiveData<String>
        get() = _instruction

    private var _peers: MutableLiveData<MutableList<WifiP2pDevice>> = MutableLiveData(mutableListOf())
    val peers: LiveData<MutableList<WifiP2pDevice>>
        get() = _peers

    private var _connected = MutableLiveData<Int>()
    val connected: LiveData<Int>
        get() = _connected

    // Functions
    fun initialiseManager(manager: WifiP2pManager, channel: WifiP2pManager.Channel, receiver: WifiDirectBroadcastReceiver) {
        this.manager = manager
        this.channel = channel
        this.receiver = receiver
    }

    fun sendBullet(bullet: Bullet) {
        val message = "bullet${bullet.positionX}"
        sendMessage(message)
    }

    /**
     * Turns on WiFi P2P discovery.
     */
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

    /**
     * Connects to a device with WiFi P2P.
     */
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

    /**
     * Sends a message to paired device.
     */
    private fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("sendMessage", "Sending message")
                if (groupOwner) {
                    server.write(message.toByteArray())
                } else {
                    client.write(message.toByteArray())
                }
            } catch (e: UninitializedPropertyAccessException) {
                Log.e("WifiViewModel", "Tried to send message before connection was made.")
            }
        }
    }

    /**
     * Function is called by WifiDirectBroadcastReceiver when connection is lost.
     */
    override fun notConnected() {
        _connected.value = NOT_CONNECTED
    }


    // Interface values
    /**
     * Callback function for when available peers changes.
     */
    override val peerListListener: WifiP2pManager.PeerListListener = WifiP2pManager.PeerListListener {
        if (it != peers) {
            _peers.value!!.clear()
            _peers.value!!.addAll(it.deviceList)
            _peers.value = _peers.value

            Log.d("peers", "hello")
        }
    }

    /**
     * Callback function for when connected peers changes.
     */
    override val connectionInfoListener: WifiP2pManager.ConnectionInfoListener = WifiP2pManager.ConnectionInfoListener {
        val groupOwnerAddress = it.groupOwnerAddress
        if (it.groupFormed && it.isGroupOwner) {
            groupOwner = true
            _connected.value = CONNECTED
            server = Server()
            server.start()
            Log.d("Wifi", "Owner")
        } else if (it.groupFormed) {
            groupOwner = false
            _connected.value = CONNECTED
            client = Client(groupOwnerAddress)
            client.start()
            Log.d("Wifi", "Client")
        }
    }

    inner class Client(hostAddress: InetAddress) : Thread() {
        private lateinit var inputStream: InputStream
        private lateinit var outputStream: OutputStream
        var hostAdd: String = hostAddress.hostAddress

        init {
            socket = Socket()
        }

        fun write(bytes: ByteArray) {
            Log.d("Write", String(bytes))
            outputStream.write(bytes)
        }

        override fun run() {
            try {
                socket.connect(InetSocketAddress(hostAdd, 8888), 500)
                inputStream = socket.getInputStream()
                outputStream = socket.getOutputStream()
            } catch (e : IOException) {
                e.printStackTrace()
            }

            val executor: ExecutorService = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            executor.run {
                val buffer = ByteArray(1024)
                var bytes : Int

                while (socket != null) {
                    try {
                        bytes = inputStream.read(buffer)
                        if (bytes > 0) {
                            val finalBytes = bytes
                            val runnable = Runnable {
                                val tempMSG = String(buffer, 0, finalBytes)
                                _instruction.value = tempMSG
                                Log.d("Valueinst", tempMSG)
                            }
                            handler.post(runnable)
                        }
                    } catch (e : IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    inner class Server() : Thread() {
        lateinit var serverSocket: ServerSocket
        private lateinit var inputStream: InputStream
        private lateinit var outputStream: OutputStream

        fun write(bytes: ByteArray) {
            Log.d("Write", String(bytes))
            outputStream.write(bytes)
        }

        override fun run() {
            try {
                serverSocket = ServerSocket(8888)
                socket = serverSocket.accept()
                inputStream = socket.getInputStream()
                outputStream = socket.getOutputStream()
            } catch (e : IOException) {
                e.printStackTrace()
            }

            val executor: ExecutorService = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            executor.run {
                val buffer = ByteArray(1024)
                var bytes : Int

                while(socket!=null) {
                    try {
                        bytes = inputStream.read(buffer)
                        if (bytes > 0) {
                            val finalBytes = bytes
                            val runnable = Runnable {
                                val tempMSG = String(buffer, 0, finalBytes)
                                _instruction.value = tempMSG
                                Log.d("Valueinst", tempMSG)
                            }
                            handler.post(runnable)
                        }
                    } catch (e : IOException) {
                        e.printStackTrace()
                    }
                }
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
    }
}