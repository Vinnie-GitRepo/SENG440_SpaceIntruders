package com.example.spaceintruders.fragments.recordFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.data.GameRecord

class GameRecordAdapter(private val onGameRecordListener: OnGameRecordListener)
    : RecyclerView.Adapter<GameRecordAdapter.GameRecordViewHolder>() {

    private var values: List<GameRecord> = emptyList()

    class GameRecordViewHolder(itemView: View, private val onGameRecordListener: OnGameRecordListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private lateinit var gameRecord: GameRecord

        private val textViewHomePlayerName: TextView
        private val textViewHomePlayerScore: TextView

        private val textViewVisitPlayerName: TextView
        private val textViewVisitPlayerScore: TextView

        private val shareBtn: Button

        init {
            textViewHomePlayerName = itemView.findViewById(R.id.home_player_name_text)
            textViewHomePlayerScore = itemView.findViewById(R.id.home_player_score_text)

            textViewVisitPlayerName = itemView.findViewById(R.id.visit_player_name_text)
            textViewVisitPlayerScore = itemView.findViewById(R.id.visit_player_score_text)

            shareBtn = itemView.findViewById(R.id.scores_share_button)
            shareBtn.setOnClickListener {
                onGameRecordListener.onGameRecordClick(gameRecord)
            }
        }

        fun setup(gameRecord: GameRecord) {
            this.gameRecord = gameRecord
            textViewHomePlayerName.text = gameRecord.homePlayerName
            textViewHomePlayerScore.text = gameRecord.homePlayerScore.toString()

            textViewVisitPlayerName.text = gameRecord.visitPlayerName
            textViewVisitPlayerScore.text = gameRecord.visitPlayerScore.toString()
        }

        override fun onClick(v: View?) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_record_item, parent, false)
        return GameRecordViewHolder(view, onGameRecordListener)
    }

    fun setData(newData: List<GameRecord>) {
        values = newData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: GameRecordViewHolder, position: Int) {
        viewHolder.setup(values[position])
    }

    override fun getItemCount() = values.size

    interface OnGameRecordListener {
        fun onGameRecordClick(gameRecord: GameRecord)
    }
}