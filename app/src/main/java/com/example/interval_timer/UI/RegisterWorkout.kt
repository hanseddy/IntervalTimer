package com.example.interval_timer.UI

import android.app.AlertDialog
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interval_timer.R
import com.example.interval_timer.database.Timer
import com.example.interval_timer.database.TimerDatabase
import com.example.interval_timer.recyclerView.TimerAdapter
import com.example.interval_timer.repository.TimerRepository
import com.example.interval_timer.viewmodel.TimerViewmodel


class RegisterWorkout : Fragment() {
    //viewmodel
    lateinit var model: TimerViewmodel
    //database
    lateinit var database: TimerDatabase
    //navigation component
    //two digit format
    lateinit var TwoDigitFormat: NumberFormat
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /**
         * back button management
         */
        view.findViewById<ImageButton>(R.id.Back_button).setOnClickListener {
            view.findNavController().navigate(R.id.homeTimer)
        }
        // Database instanciation
        database= TimerDatabase.getDatabase(requireContext())
        // Repository
        val repository: TimerRepository = TimerRepository(database.timerDao())
        //viewmodel instantiation
        model= ViewModelProvider(this,
            TimerViewmodel.WordViewmodelFactory(repository))[TimerViewmodel::class.java]
        TwoDigitFormat = DecimalFormat("00")

        recyclerView = view.findViewById<RecyclerView>(R.id.Timer_RecyclerView)
        recyclerView.layoutManager=  LinearLayoutManager(requireContext())

        model.allWords.observe(viewLifecycleOwner, Observer { timerList ->
            val Timeradapter = TimerAdapter(timerList as ArrayList<Timer>)
            recyclerView.adapter = Timeradapter
        })

    }
}