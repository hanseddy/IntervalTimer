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

   val workRealTime:LiveData<Int> =MutableLiveData<Int>()
   val restRealTime:LiveData<Int> =MutableLiveData<Int>()
   val roundRealTime:LiveData<Int> =MutableLiveData<Int>()



}