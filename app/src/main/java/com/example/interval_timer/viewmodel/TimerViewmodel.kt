package com.example.interval_timer.viewmodel

import androidx.lifecycle.*
import com.example.interval_timer.database.Timer
import com.example.interval_timer.repository.TimerRepository
import kotlinx.coroutines.launch

class TimerViewmodel(val repository: TimerRepository):ViewModel() {
    val allWords: LiveData<List<Timer>> = repository.getAllWord.asLiveData()

    fun insert(timer: Timer) = viewModelScope.launch {
        repository.insertTimer(timer)
    }

    class WordViewmodelFactory(private val repository: TimerRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return  TimerViewmodel(repository) as T//modelClass.getConstructor(repository::class.java).newInstance(repository)
        }
    }
    //
   val configworkTime =MutableLiveData<Int>()
   val configrestTime=MutableLiveData<Int>()
   val configroundTime=MutableLiveData<Int>()

    // paused data
    val numRoundLeft =MutableLiveData<Int>()
    val workTimeLeft =MutableLiveData<Int>()
    val restTimeLeft =MutableLiveData<Int>()

    //getter & setter

    // numRoundLeft getter & setter
    fun getnumRoundLeft(): Int? {
        return numRoundLeft.value
    }
    fun setnumRoundLeft(leftRound:Int){
        numRoundLeft.value=leftRound
    }

    // workTimeLeft getter & setter
    fun getworkTimeLeft(): Int? {
        return workTimeLeft.value
    }
    fun setworkTimeLeft(leftRound:Int){
        workTimeLeft.value=leftRound
    }

    // restTimeLeft getter & setter
    fun getrestTimeLeft(): Int {
        return restTimeLeft.value!!
    }
    fun setrestTimeLeft(leftRound:Int){
        restTimeLeft.value=leftRound
    }

// getter & setter config data
    //work
    fun getconfigworkTime(): Int? {
        return configworkTime.value
    }
    fun setconfigworkTime(workTime:Int){
        configworkTime.value=workTime
    }
    //rest
    fun getconfigrestTime(): Int? {
        return configrestTime.value
    }
    fun setconfigrestTime(workTime:Int){
        configrestTime.value=workTime
    }
    //round

    fun getconfigroundTime(): Int? {
        return configroundTime.value
    }
    fun setconfigroundTime(workTime:Int){
        configroundTime.value=workTime
    }

}