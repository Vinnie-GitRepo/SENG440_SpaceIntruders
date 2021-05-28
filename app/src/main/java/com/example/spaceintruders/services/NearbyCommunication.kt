package com.example.spaceintruders.services

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyCommunication(private val context: Context) {
    private val payloadListener = object : PayloadCallback() {
        override fun onPayloadReceived(id: String, payload: Payload) {
            payload.asBytes()?.let { bytes ->

            }
        }

        override fun onPayloadTransferUpdate(id: String, update: PayloadTransferUpdate) {}
    }

    private val connectionListener = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(id: String, info: ConnectionInfo) {
            Toast.makeText(context, "Connecting to ${info.endpointName}.", Toast.LENGTH_SHORT).show()
            Nearby.getConnectionsClient(context).acceptConnection(id, payloadListener)
        }

        override fun onConnectionResult(endpoint: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show()
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDisconnected(endpoint: String) {
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun advertise() {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context).startAdvertising("Ben", "P2Piano", connectionListener, options)
            .addOnSuccessListener {
                Toast.makeText(context, "Advertising...", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to advertise...", Toast.LENGTH_LONG).show()
            }
    }

    private fun createDiscoverListener(items: MutableList<Endpoint>, adapter: ArrayAdapter<Endpoint>): EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                items.add(Endpoint(id, info))
                adapter.notifyDataSetChanged()
            }

            override fun onEndpointLost(p0: String) {
                items.removeIf { it.id == p0 }
                adapter.notifyDataSetChanged()
            }
        }
    }

    // Exercise
    private fun discover() {
        val items = mutableListOf<Endpoint>()
        val adapter = showEndpointChooser(items)
        val callback = createDiscoverListener(items, adapter)

        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(this).startDiscovery("P2Piano", callback, options).addOnSuccessListener {
            Toast.makeText(this, "Looking for hosts...", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Discovery failed!", Toast.LENGTH_LONG).show()
        }
    }

    // Exercise
    override fun sendMidiMessage(bytes: ByteArray) {
        super.sendMidiMessage(bytes)
        hostEndpoint?.let {
            Nearby.getConnectionsClient(this).sendPayload(it, Payload.fromBytes(bytes))
        }
    }
}