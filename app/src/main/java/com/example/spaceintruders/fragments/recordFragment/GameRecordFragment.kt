package com.example.spaceintruders.fragments.recordFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
import com.example.spaceintruders.data.GameRecord
import com.example.spaceintruders.viewmodels.GameViewModel

class GameRecordFragment : Fragment(), GameRecordAdapter.OnGameRecordListener {
    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_record_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.game_record_recycler_view)
        val adapter = GameRecordAdapter(this)
        recyclerView.adapter = adapter

        gameViewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        gameViewModel.readAllData.observe(viewLifecycleOwner) { newData ->
            adapter.setData(newData)
        }


        return view
    }


    override fun onGameRecordClick(gameRecord: GameRecord) {
        var result = ""
        result = if (gameRecord.homePlayerScore == 3){
            resources.getString(R.string.victory_text)
        } else {
            resources.getString(R.string.defeat_text)
        }
        val shareText = String.format(resources.getString(R.string.share_text_template, gameRecord.visitPlayerName, result))

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

}