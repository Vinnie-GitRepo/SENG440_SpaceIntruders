package com.example.spaceintruders.fragments.recordFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.data.GameRecord

//class GameRecordAdapter(private val gameRecords: Array<GameRecord>, private val onGameRecordListener: OnGameRecordListener)
//    : RecyclerView.Adapter<GameRecordAdapter.GameRecordViewHolder>() {
//
//
//    class GameRecordViewHolder(itemView: View, private val onGameRecordListener: OnGameRecordListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
//
//        val textViewHomePlayerName: TextView
//        val textViewHomePlayerScore: TextView
//
//        val textViewVisitPlayerName: TextView
//        val textViewVisitPlayerScore: TextView
//
//        init {
//            textViewHomePlayerName = itemView.findViewById(R.id.home_player_name_text)
//            textViewHomePlayerScore = itemView.findViewById(R.id.home_player_score_text)
//
//            textViewVisitPlayerName = itemView.findViewById(R.id.visit_player_name_text)
//            textViewVisitPlayerScore = itemView.findViewById(R.id.visit_player_score_text)
//
//
//        }
//
//        override fun onClick(view: View?) {
//            onGameRecordListener.onGameRecordClick(adapterPosition)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameRecordViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_record_item, parent, false)
//        return GameRecordViewHolder(view, onGameRecordListener)
//    }
//
//    override fun onBindViewHolder(viewHolder: GameRecordViewHolder, position: Int) {
//        viewHolder.textViewHomePlayerName.text = gameRecords[position].homePlayerName
//        viewHolder.textViewHomePlayerScore.text = gameRecords[position].homePlayerName
//
//        viewHolder.textViewVisitPlayerName.text = gameRecords[position].homePlayerName
//        viewHolder.textViewVisitPlayerScore.text = gameRecords[position].homePlayerName
//    }
//
//    override fun getItemCount() = gameRecords.size
//
//    interface OnGameRecordListener {
//        fun onGameRecordClick(position: Int)
//    }
//}