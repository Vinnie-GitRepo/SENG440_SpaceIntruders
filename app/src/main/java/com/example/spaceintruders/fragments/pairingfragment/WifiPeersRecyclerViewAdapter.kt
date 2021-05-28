package com.example.spaceintruders.fragments.pairingfragment

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.services.Endpoint

class WifiPeersRecyclerViewAdapter(private val connectListener: OnConnectListener): RecyclerView.Adapter<WifiPeersRecyclerViewAdapter.ItemViewHolder>() {
    val values: ArrayList<Endpoint> = arrayListOf()

    class ItemViewHolder(view: View, connectListener: OnConnectListener) : RecyclerView.ViewHolder(view) {
        lateinit var endpoint: Endpoint
        val deviceNameText: TextView = view.findViewById(R.id.peer_deviceName)
        private val deviceConnect: Button = view.findViewById(R.id.peer_connectBtn)
        init {
            deviceConnect.setOnClickListener {
                connectListener.onConnClick(endpoint)
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
        holder.endpoint = values[position]
        holder.deviceNameText.text = values[position].name
    }

//    fun setData(data: List<Endpoint>) {
//        values = data
//        notifyDataSetChanged()
//    }

    interface OnConnectListener {
        fun onConnClick(device: Endpoint)
    }
}