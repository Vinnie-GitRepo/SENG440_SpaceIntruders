package com.example.spaceintruders.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceintruders.data.GameRecord
import com.example.spaceintruders.data.GameRecordDatabase
import com.example.spaceintruders.data.GameRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<GameRecord>>

    private val _scoreHomePlayer = MutableLiveData(0)
    val scoreHomePlayer: LiveData<Int>
        get() = _scoreHomePlayer

    private val _scoreVisitPlayer = MutableLiveData(0)
    val scoreVisitPlayer: LiveData<Int>
        get() = _scoreVisitPlayer

    private val repository: GameRecordRepository


    init {
        val gameRecordDao = GameRecordDatabase.getDatabase(application).gameRecordDao()
        repository = GameRecordRepository(gameRecordDao)
        readAllData = repository.readAllData
    }

    fun addGameRecord(homePlayerName: String, visitPlayerName: String) {
        val gameRecord = GameRecord(null, homePlayerName, scoreHomePlayer.value!!, visitPlayerName, scoreVisitPlayer.value!!)
        viewModelScope.launch(Dispatchers.IO) {
            repository.addGameRecord(gameRecord)
        }
    }

    fun enemyScored() {
        _scoreVisitPlayer.postValue(_scoreVisitPlayer.value!!.plus(1))
    }

    fun homeScored() {
        _scoreHomePlayer.postValue(_scoreHomePlayer.value!!.plus(1))
    }

    fun reset() {
        _scoreHomePlayer.value = 0
        _scoreVisitPlayer.value = 0
    }
}