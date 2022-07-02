package com.example.interval_timer.repository

import com.example.interval_timer.database.Timer
import com.example.interval_timer.database.TimerDao
import kotlinx.coroutines.flow.Flow

class TimerRepository(private val timerDao: TimerDao) {

    val getAllWord: Flow<List<Timer>> = timerDao.getAllTimer()

    suspend fun insertTimer(timer: Timer) {
        timerDao.insertTimer(timer)
    }
}