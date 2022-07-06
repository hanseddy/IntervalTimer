package com.example.interval_timer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Query("SELECT * FROM timer ORDER BY id ASC")
    fun getAllTimer():Flow<List<Timer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(timer:Timer)

    @Query("DELETE FROM timer")
    suspend fun deleteAll()

    @Query("SELECT * FROM timer WHERE id =:id")
    suspend fun getTimerAt(id: Int):Timer
}