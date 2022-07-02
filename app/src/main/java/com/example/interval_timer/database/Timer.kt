package com.example.interval_timer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer")
data class Timer(@PrimaryKey(autoGenerate = true)
                 val id:Int,
                 @ColumnInfo(name= "workoutName")
                 val WorkoutName:String,
                 @ColumnInfo(name = "workoutTime")
                 val WorkoutTime:Int,    //in second
                 @ColumnInfo(name = "restTime")
                 val RestTime:Int ,
                 @ColumnInfo(name = "round")
                 val round:Int )   //in second