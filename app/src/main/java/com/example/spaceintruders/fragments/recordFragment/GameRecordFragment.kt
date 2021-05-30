package com.example.spaceintruders.fragments.recordFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceintruders.R
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


        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onGameRecordClick(position: Int) {
        val toast = Toast.makeText(requireContext(), "Replace me with options", Toast.LENGTH_SHORT)
        toast.show()
    }

}