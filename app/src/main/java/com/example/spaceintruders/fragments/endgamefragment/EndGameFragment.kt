package com.example.spaceintruders.fragments.endgamefragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.spaceintruders.R
import com.example.spaceintruders.viewmodels.NearbyCommunication
import com.example.spaceintruders.viewmodels.GameViewModel
import org.w3c.dom.Text

/**
 *
 */
class EndGameFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private val nearbyCommunication: NearbyCommunication by activityViewModels() //Get names from here
    private lateinit var mainMenuButton: Button
    private lateinit var homeName: TextView
    private lateinit var visitName: TextView
    private lateinit var homeScore: TextView
    private lateinit var visitScore: TextView

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

        homeName = view.findViewById(R.id.nameHome)
        visitName = view.findViewById(R.id.nameVisit)
        homeScore = view.findViewById(R.id.scoreHome)
        visitScore = view.findViewById(R.id.scoreVisit)

        homeName.text = getString(R.string.you)
        visitName.text = nearbyCommunication.getOpponentName()!!
        homeScore.text = gameViewModel.scoreHomePlayer.value.toString()
        visitScore.text = gameViewModel.scoreVisitPlayer.value.toString()

        gameViewModel.addGameRecord(getString(R.string.you), nearbyCommunication.getOpponentName()!!)
//        gameViewModel.addGameRecord(getString(R.string.you), PreferenceManager.getDefaultSharedPreferences(application).getString("username", "client"))

        return view
    }


    private fun returnToHomeScreen() {
        findNavController().navigate(R.id.action_endGameFragment_to_menuFragment2)
        gameViewModel.reset()
        nearbyCommunication.disconnect(requireContext())
        //TODO @@@@@@@@ Add saving of game stats @@@@@@@@
    }
}