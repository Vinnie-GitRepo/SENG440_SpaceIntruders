package com.example.spaceintruders.data

import androidx.lifecycle.LiveData


class GameRecordRepository(private val gameRecordDao: GameRecordDao) {

    val readAllData: LiveData<List<GameRecord>> = gameRecordDao.readAllData()

    suspend fun addGameRecord(gameRecord: GameRecord) {
        gameRecordDao.addGameRecord(gameRecord)
    }

    fun getAllGameRecords() : LiveData<List<GameRecord>> {
        return gameRecordDao.readAllData()
    }

}