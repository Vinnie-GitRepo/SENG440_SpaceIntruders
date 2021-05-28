package com.example.spaceintruders.services

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

class Endpoint(val id: String, private val info: DiscoveredEndpointInfo) {
    val name
        get() = info.endpointName
    override fun toString() = name
}
