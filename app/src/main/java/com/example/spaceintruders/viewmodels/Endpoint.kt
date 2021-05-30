package com.example.spaceintruders.viewmodels

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

class Endpoint(val id: String, private val info: DiscoveredEndpointInfo) {
    val name
        get() = info.endpointName
    override fun toString() = name
}
