package com.example.spaceintruders.fragments.menufragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.spaceintruders.R
import com.example.spaceintruders.activities.SettingsActivity
import com.example.spaceintruders.activities.TutorialActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment for the Main Menu
 */
class MenuFragment : Fragment() {
    private lateinit var playButton: Button
    private lateinit var scoreButton: Button
    private lateinit var settingsButton: FloatingActionButton
    private lateinit var tutorialButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        // Assign widget values
        playButton = view.findViewById(R.id.home_PlayButton)
        scoreButton = view.findViewById(R.id.home_ScoresButton)
        settingsButton = view.findViewById(R.id.home_SettingsButton)
        tutorialButton = view.findViewById(R.id.home_tutorial)

        playButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_menuFragment_to_pairingFragment, null)
        )

        settingsButton.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        scoreButton.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_gameRecordFragment)
        }

        tutorialButton.setOnClickListener {
            val intent = Intent(requireContext(), TutorialActivity::class.java)
            startActivity(intent)
        }

        return view
    }




}