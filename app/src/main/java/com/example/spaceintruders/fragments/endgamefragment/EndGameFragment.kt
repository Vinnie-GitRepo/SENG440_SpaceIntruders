package com.example.spaceintruders.fragments.endgamefragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spaceintruders.R
import com.example.spaceintruders.services.NearbyCommunication
import com.example.spaceintruders.util.AppUtil
import com.example.spaceintruders.viewmodels.GameViewModel

/**
 *
 */
class EndGameFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private val nearbyCommunication: NearbyCommunication by activityViewModels() //Get names from here
    private lateinit var mainMenuButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_end_game, container, false)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                returnToHomeScreen()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        mainMenuButton = view.findViewById(R.id.endGame_returnButton)
        mainMenuButton.setOnClickListener {
            returnToHomeScreen()
        }
        return view
    }


    private fun returnToHomeScreen() {
        findNavController().navigate(R.id.action_endGameFragment_to_menuFragment2)
        gameViewModel.reset()
        nearbyCommunication.disconnect(requireContext())
        //TODO @@@@@@@@ Add saving of game stats @@@@@@@@
    }
}