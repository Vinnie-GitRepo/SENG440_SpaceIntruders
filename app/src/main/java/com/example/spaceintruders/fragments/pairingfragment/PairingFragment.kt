package com.example.spaceintruders.fragments.pairingfragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.activities.MainActivity
import com.example.spaceintruders.services.Endpoint
import com.example.spaceintruders.viewmodels.WifiViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*


/**
 * A simple [Fragment] subclass.
 * Use the [PairingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PairingFragment : Fragment(), WifiPeersRecyclerViewAdapter.OnConnectListener {
    private lateinit var discoverButton: Button
    private lateinit var statusText: TextView
    private lateinit var peersListView: RecyclerView
    private lateinit var loadingCircle: ProgressBar
    private val adapter = WifiPeersRecyclerViewAdapter(this)

    private val payloadListener = object : PayloadCallback() {
        override fun onPayloadReceived(endpoint: String, payload: Payload) {}
        override fun onPayloadTransferUpdate(endpoint: String, update: PayloadTransferUpdate) {}
    }

    private val connectionListener = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(id: String, info: ConnectionInfo) {
            Nearby.getConnectionsClient(requireActivity()).acceptConnection(id, payloadListener)
        }

        override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                }
            }
        }

        override fun onDisconnected(endpoint: String) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requireActivity().requestPermissions(permissions, 100{
            advertise()
        }, {
            Toast.makeText(this, "Location not permitted.", Toast.LENGTH_LONG).show()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pairing, container, false)

        // Assign widget values
        statusText = view.findViewById(R.id.pair_StatusText)
        discoverButton = view.findViewById(R.id.pair_DiscoverBtn)
        peersListView = view.findViewById(R.id.peer_recyclerView)
        loadingCircle = view.findViewById(R.id.pairing_progressBar)

        loadingCircle.visibility = View.INVISIBLE

        discoverButton.setOnClickListener {
            advertise()
            discover()
        }

        peersListView.adapter = adapter
        peersListView.addItemDecoration(DividerItemDecoration(peersListView.context, DividerItemDecoration.VERTICAL))

        return view
    }

//    fun discoverPeers() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
//        }
//
//    }

    private fun advertise() {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(requireActivity()).startAdvertising("Ben", "P2Piano", connectionListener, options)
            .addOnSuccessListener {
                Log.d("Success", "Success")
            }.addOnFailureListener {
                Log.d("failure", "failure")
            }
    }

    private fun discover() {
        val callback = createDiscoverListener(adapter)

        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(requireActivity()).startDiscovery("P2Piano", callback, options).addOnSuccessListener {
            Log.d("Success", "Success")
        }.addOnFailureListener {

        }
    }

    private fun joinHost(endpoint: Endpoint) {
        Nearby.getConnectionsClient(requireActivity())
            .requestConnection("CLIENT", endpoint.id, connectionListener)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Connected to ${endpoint.name}.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Rejected by ${endpoint.name}.", Toast.LENGTH_LONG).show()
            }
    }

    private fun createDiscoverListener(adapter: WifiPeersRecyclerViewAdapter): EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                adapter.values.add(Endpoint(id, info))
                adapter.notifyDataSetChanged()
            }

            override fun onEndpointLost(p0: String) {
                adapter.values.removeIf { it.id == p0 }
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    shouldShowRequestPermissionRationale("Location is required to use WiFi direct")
                }
                return
            }
            else -> {}
        }
    }

    override fun onConnClick(device: Endpoint) {
//        wifiViewModel.connect(device)
    }
}