package com.example.spaceintruders.fragments.pairingfragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.activities.MainActivity
import com.example.spaceintruders.services.Endpoint
import com.example.spaceintruders.services.NearbyCommunication
import com.example.spaceintruders.viewmodels.GameViewModel
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

    private val nearbyCommunication: NearbyCommunication by activityViewModels()


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
            nearbyCommunication.advertise(requireContext())
            nearbyCommunication.discover(requireContext())
        }

        peersListView.adapter = adapter
        peersListView.addItemDecoration(DividerItemDecoration(peersListView.context, DividerItemDecoration.VERTICAL))

        nearbyCommunication.peers.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        nearbyCommunication.connected.observe(viewLifecycleOwner) { data ->
            when (data) {
                NearbyCommunication.CONNECTING -> {
                    statusText.text = getString(R.string.connecting)
                    loadingCircle.visibility = View.VISIBLE
                    val color = -0x5fa81b
                    loadingCircle.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                NearbyCommunication.DISCOVERING -> {
                    statusText.text = getString(R.string.peer_discovery_start)
                    loadingCircle.visibility = View.VISIBLE
                    val color = -0x1b98a8
                    loadingCircle.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
                NearbyCommunication.CONNECTED -> {
                    statusText.text = getString(R.string.peer_connected)
                    loadingCircle.visibility = View.INVISIBLE
                    val color = -0x5fa81b
                    loadingCircle.indeterminateDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                    Log.d("", "navigating to game")
                    findNavController().navigate(R.id.action_pairingFragment_to_gameFragment)
                }
                NearbyCommunication.DISCOVERY_FAILED -> {
                    statusText.text = getString(R.string.peer_discovery_fail)
                    loadingCircle.visibility = View.INVISIBLE
                }
                NearbyCommunication.NOT_CONNECTED -> {
                    statusText.text = getString(R.string.peer_discovery_inactive)
                    loadingCircle.visibility = View.INVISIBLE
                }
                NearbyCommunication.CONNECTION_FAILED -> {
                    statusText.text = getString(R.string.peer_connection_fail)
                    loadingCircle.visibility = View.INVISIBLE
                }
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissions, 100, {
        }, {
            Toast.makeText(context, "Location not permitted.", Toast.LENGTH_LONG).show()
        })
    }



    override fun onConnClick(device: Endpoint) {
        nearbyCommunication.joinHost(requireContext(), device)
    }


    /**
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     * @@ Permissions related logic @@
     * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */
    private val requests: MutableMap<Int, PermissionsRequest> = mutableMapOf()

    fun requestPermissions(permissions: Array<String>, requestId: Int, onSuccess: () -> Unit, onFailure: () -> Unit) {
        // The WRITE_SETTINGS permission must be granted using a different
        // scheme. Frustrating.
        val hasWriteSettings = permissions.contains(android.Manifest.permission.WRITE_SETTINGS)
        val needsWriteSettings = hasWriteSettings && !Settings.System.canWrite(requireContext())
        val remaining = if (hasWriteSettings) {
            permissions.filter { it != android.Manifest.permission.WRITE_SETTINGS }
        } else {
            permissions.toList()
        }

        // If we're on early Android, runtime requests are not needed,
        // so we assume permission has already been granted by listing
        // the permissions in the manifest.
        val ungranted = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> listOf()
            else -> remaining.filter { ActivityCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED }
        }

        // If all but the WRITE_SETTINGS permission has been granted...
        if (ungranted.isEmpty()) {
            if (needsWriteSettings) {
                requests[requestId] = PermissionsRequest(needsWriteSettings, onSuccess, onFailure)
                promptForWriteSettings(requestId)
            } else {
                onSuccess()
            }
        }

        // Otherwise, request the ungranted permissions.
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requests[requestId] = PermissionsRequest(needsWriteSettings, onSuccess, onFailure)
            ActivityCompat.requestPermissions(requireActivity(), ungranted.toTypedArray(), requestId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // If all the requested permissions have been granted, we're ready to
        // trigger success! Right? Maybe. We might still need WRITE_SETTINGS.
        requests[requestCode]?.let { request ->
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                if (request.needsWriteSettings) {
                    promptForWriteSettings(requestCode)
                } else {
                    request.onSuccess.invoke()
                    requests.remove(requestCode)
                }
            } else {
                request.onFailure.invoke()
                requests.remove(requestCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Our last hurdle... Did we get WRITE_SETTINGS?
        requests[requestCode]?.let { request ->
            if (Settings.System.canWrite(requireContext())) {
                request.onSuccess.invoke()
            } else {
                request.onFailure.invoke()
            }
            requests.remove(requestCode)
        }
    }

    private fun promptForWriteSettings(requestId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("This operation requires the ability to modify system settings. Please grant this permission on the next screen.")
        builder.setPositiveButton("Okay") { _, _ ->
            startActivityForResult(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:${requireContext().packageName}")), requestId)
        }
        builder.show()
    }

    class PermissionsRequest(val needsWriteSettings: Boolean, val onSuccess: () -> Unit, val onFailure: () -> Unit)
}