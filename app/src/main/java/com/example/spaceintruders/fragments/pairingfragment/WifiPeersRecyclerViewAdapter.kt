package com.example.spaceintruders.fragments.pairingfragment

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R

class WifiPeersRecyclerViewAdapter(private val connectListener: OnConnectListener): RecyclerView.Adapter<WifiPeersRecyclerViewAdapter.ItemViewHolder>() {
    private var values: List<WifiP2pDevice> = emptyList()

    class ItemViewHolder(view: View, connectListener: OnConnectListener) : RecyclerView.ViewHolder(view) {
        lateinit var wifiP2pDevice: WifiP2pDevice
        val deviceNameText: TextView = view.findViewById(R.id.peer_deviceName)
        private val deviceConnect: Button = view.findViewById(R.id.peer_connectBtn)
        init {
            deviceConnect.setOnClickListener {
                connectListener.onConnClick(wifiP2pDevice)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.peer_item, parent, false)
        return ItemViewHolder(view, connectListener)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.wifiP2pDevice = values[position]
        holder.deviceNameText.text = values[position].deviceName
    }

    fun setData(data: List<WifiP2pDevice>) {
        values = data
        notifyDataSetChanged()
    }

    interface OnConnectListener {
        fun onConnClick(device: WifiP2pDevice)
    }
}