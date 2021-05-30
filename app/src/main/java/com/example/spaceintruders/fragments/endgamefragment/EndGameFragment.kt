package com.example.spaceintruders.fragments.endgamefragment

import android.content.Intent
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

        var opponent = visitName.text
        var result = ""
        if(gameViewModel.scoreHomePlayer.value.toString() == "3"){
            result = resources.getString(R.string.victory_text)
        } else {
            result = resources.getString(R.string.defeat_text)
        }

        val shareButton = view.findViewById<Button>(R.id.shareButton)
        shareButton.setOnClickListener {
            var shareText = String.format(resources.getString(R.string.share_text_template, opponent, result))

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        return view
    }


    private fun returnToHomeScreen() {
        findNavController().navigate(R.id.action_endGameFragment_to_menuFragment2)
        gameViewModel.addGameRecord(getString(R.string.you), gameViewModel.scoreHomePlayer.value!!, nearbyCommunication.getOpponentName()!!, gameViewModel.scoreVisitPlayer.value!!)
        gameViewModel.reset()
        nearbyCommunication.disconnect(requireContext())

    }
}