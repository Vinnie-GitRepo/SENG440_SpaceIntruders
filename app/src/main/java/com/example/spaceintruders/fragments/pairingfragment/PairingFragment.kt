package com.example.spaceintruders.fragments.pairingfragment

import android.Manifest
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.viewmodels.WifiViewModel
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 * Use the [PairingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PairingFragment : Fragment(), WifiPeersRecyclerViewAdapter.OnConnectListener {
    private lateinit var discoverButton: Button
    private lateinit var statusText: TextView
    private lateinit var peersListView: RecyclerView
    private val adapter = WifiPeersRecyclerViewAdapter(this)

    private val wifiViewModel: WifiViewModel by activityViewModels()

    // TODO this is test stuff here
    private lateinit var receive: TextView
    private lateinit var send: Button

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

        discoverButton.setOnClickListener {
            discoverPeers()
        }

        wifiViewModel.connected.observe(viewLifecycleOwner) { data ->
            when (data) {
                WifiViewModel.CONNECTED -> statusText.text = getString(R.string.peer_connected)
                WifiViewModel.DISCOVERING -> statusText.text = getString(R.string.peer_discovery_start)
                WifiViewModel.DISCOVERY_FAILED -> statusText.text = getString(R.string.peer_discovery_fail)
                WifiViewModel.NOT_CONNECTED -> statusText.text = getString(R.string.peer_discovery_inactive)
                WifiViewModel.CONNECTION_FAILED -> statusText.text = getString(R.string.peer_connection_fail)
            }
        }

        wifiViewModel.peers.observe(viewLifecycleOwner) {
            adapter.setData(it)
            print("here")
        }

        peersListView.adapter = adapter
        peersListView.addItemDecoration(DividerItemDecoration(peersListView.context, DividerItemDecoration.VERTICAL))

        testItems(view)

        return view
    }

    fun testItems(view: View) {
        receive = view.findViewById(R.id.receive)
        send = view.findViewById(R.id.send)
        send.setOnClickListener {
            Log.d("Here", "here")
            wifiViewModel.sendMessage("hello there!")
        }
        wifiViewModel.instruction.observe(viewLifecycleOwner) {
            receive.text = it
        }
    }

    fun discoverPeers() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        wifiViewModel.discoverPeers(requireContext())
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
                    discoverPeers()
                } else {
                    shouldShowRequestPermissionRationale("Location is required to use WiFi direct")
                }
                return
            }
            else -> {}
        }
    }

    override fun onConnClick(device: WifiP2pDevice) {
        wifiViewModel.connect(device)
    }
}