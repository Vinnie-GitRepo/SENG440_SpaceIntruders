package com.example.spaceintruders.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GameRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addGameRecord(gameRecord: GameRecord)

    @Update
    suspend fun updateGameRecord(gameRecord: GameRecord)

    @Query("SELECT * FROM gamerecord ORDER BY id ASC")
    fun readAllData(): LiveData<List<GameRecord>>
}