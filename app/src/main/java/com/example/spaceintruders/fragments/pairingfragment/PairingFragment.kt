package com.example.spaceintruders.fragments.pairingfragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.services.WifiDirectBroadcastReceiver
import com.example.spaceintruders.viewmodels.WifiViewModel


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
    private lateinit var portInput: TextView
    private val adapter = WifiPeersRecyclerViewAdapter(this)

    private val wifiViewModel: WifiViewModel by activityViewModels()

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
        portInput = view.findViewById(R.id.portInput)
        portInput.text = "8888"

        portInput.addTextChangedListener(object : TextWatcher {
            var text = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                wifiViewModel.setPort(text.toInt())
            }
        })

        loadingCircle.visibility = View.INVISIBLE

        discoverButton.setOnClickListener {
            discoverPeers()
        }

        wifiViewModel.connected.observe(viewLifecycleOwner) { data ->
            when (data) {
                WifiViewModel.PORT_IN_USE -> {

                }
                WifiViewModel.CONNECTING -> {
                    statusText.text = getString(R.string.connecting)
                    loadingCircle.visibility = View.VISIBLE
                    val color = -0x5fa81b
                    loadingCircle.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                WifiViewModel.DISCOVERING -> {
                    statusText.text = getString(R.string.peer_discovery_start)
                    loadingCircle.visibility = View.VISIBLE
                    val color = -0x1b98a8
                    loadingCircle.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                WifiViewModel.CONNECTED -> {
                    statusText.text = getString(R.string.connecting)
                    loadingCircle.visibility = View.VISIBLE
                    val color = -0x5fa81b
                    loadingCircle.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                WifiViewModel.DISCOVERY_FAILED -> {
                    statusText.text = getString(R.string.peer_discovery_fail)
                    loadingCircle.visibility = View.INVISIBLE
                }
                WifiViewModel.NOT_CONNECTED -> {
                    statusText.text = getString(R.string.peer_discovery_inactive)
                    loadingCircle.visibility = View.INVISIBLE
                }
                WifiViewModel.CONNECTION_FAILED -> {
                    statusText.text = getString(R.string.peer_connection_fail)
                    loadingCircle.visibility = View.INVISIBLE
                }
            }
        }

        wifiViewModel.peers.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        wifiViewModel.instruction.observe(viewLifecycleOwner) {
            if (it == "ready" && wifiViewModel.connected.value == WifiViewModel.CONNECTED) {
                Navigation.findNavController(view).navigate(R.id.action_pairingFragment_to_gameFragment)
                wifiViewModel.resetInstruction()
            }
        }

        peersListView.adapter = adapter
        peersListView.addItemDecoration(DividerItemDecoration(peersListView.context, DividerItemDecoration.VERTICAL))

        return view
    }

    private fun discoverPeers() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
        val manager = requireActivity().getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel = manager.initialize(context, Looper.getMainLooper(), null)
        val receiver = WifiDirectBroadcastReceiver(manager, channel, wifiViewModel)
        wifiViewModel.initialiseManager(manager, channel, receiver)
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