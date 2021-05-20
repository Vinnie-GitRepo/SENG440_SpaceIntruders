package com.example.spaceintruders.fragments.menufragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.spaceintruders.R
import com.example.spaceintruders.activities.SettingsActivity
import com.example.spaceintruders.viewmodels.WifiViewModel

/**
 * Fragment for the Main Menu
 */
class MenuFragment : Fragment() {
    private val wifiViewModel: WifiViewModel by activityViewModels()
    private lateinit var playButton: Button
    private lateinit var scoreButton: Button
    private lateinit var settingsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        // Assign widget values
        playButton = view.findViewById(R.id.home_PlayButton)
        scoreButton = view.findViewById(R.id.home_ScoresButton)
        settingsButton = view.findViewById(R.id.home_SettingsButton)

        playButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_menuFragment_to_pairingFragment, null)
        )

        settingsButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }




}