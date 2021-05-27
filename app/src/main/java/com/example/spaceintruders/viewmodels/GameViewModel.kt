package com.example.spaceintruders.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val _scoreHomePlayer = MutableLiveData(0)
    val scoreHomePlayer: LiveData<Int>
        get() = _scoreHomePlayer

    private val _scoreVisitPlayer = MutableLiveData(0)
    val scoreVisitPlayer: LiveData<Int>
        get() = _scoreVisitPlayer

    fun enemyScored() {
        _scoreVisitPlayer.value = _scoreVisitPlayer.value!!.plus(1)
    }

    fun homeScored() {
        _scoreHomePlayer.value = _scoreHomePlayer.value!!.plus(1)
    }
}