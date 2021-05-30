package com.example.spaceintruders.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class for objects containing information about a previous game outcome.
 * Stores the names of the players who played the game and their scores.
 */
@Entity
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,

    //  Phone owner details
    val homePlayerName: String,
    val homePlayerScore: Int,

    //  Opponent details
    val visitPlayerName: String,
    val visitPlayerScore: Int
)