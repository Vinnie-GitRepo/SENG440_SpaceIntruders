package com.example.spaceintruders.fragments.pairingfragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.example.spaceintruders.R
import com.example.spaceintruders.viewmodels.WiFiViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [PairingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PairingFragment : Fragment() {
    private val wifiViewModel: WiFiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pairing, container, false)
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
}