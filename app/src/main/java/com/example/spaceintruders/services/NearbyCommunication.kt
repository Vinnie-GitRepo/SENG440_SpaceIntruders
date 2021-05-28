package com.example.spaceintruders.services

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

class NearbyCommunication() {
    private var hostEndpoint: String? = null

    private val payloadListener = object : PayloadCallback() {
        override fun onPayloadReceived(id: String, payload: Payload) {
            payload.asBytes()?.let { bytes ->

            }
        }

        override fun onPayloadTransferUpdate(id: String, update: PayloadTransferUpdate) {}
    }





    // Exercise


    // Exercise
//    fun sendMessage(bytes: ByteArray) {
//        super.sendMidiMessage(bytes)
//        hostEndpoint?.let {
//            Nearby.getConnectionsClient(context).sendPayload(it, Payload.fromBytes(bytes))
//        }
//    }
}