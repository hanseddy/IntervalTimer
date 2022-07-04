package com.example.interval_timer.UI

import android.app.AlertDialog
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.interval_timer.R
import com.example.interval_timer.database.Timer
import com.example.interval_timer.database.TimerDatabase
import com.example.interval_timer.repository.TimerRepository
import com.example.interval_timer.viewmodel.TimerViewmodel


class FinishedWorkout : Fragment() {
    var WorkoutName:String="unknown"
    val args:FinishedWorkoutArgs by navArgs()
    //viewmodel
    lateinit var model: TimerViewmodel
    //database
    lateinit var database:TimerDatabase
    //navigation component
    //two digit format
    lateinit var TwoDigitFormat: NumberFormat
    //lateinit var button: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finished_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Database instanciation
        database= TimerDatabase.getDatabase(requireContext())
        // Repository
        val repository: TimerRepository = TimerRepository(database.timerDao())
        //viewmodel instantiation
        model= ViewModelProvider(this,
            TimerViewmodel.WordViewmodelFactory(repository))[TimerViewmodel::class.java]

       val button=view.findViewById<ImageButton>(R.id.register_wo_finishedWO)
        TwoDigitFormat = DecimalFormat("00")
          button.setOnClickListener {
             val builder = AlertDialog.Builder(context)
             builder.setTitle("Save the workout")
              val inflater:LayoutInflater=layoutInflater
              val dialogLayout=inflater.inflate(R.layout.popup_editname,null)

              populateUI(dialogLayout)





             builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                 /*Toast.makeText(context,
                     android.R.string.yes, Toast.LENGTH_SHORT).show()*/
                 val editText:EditText= dialogLayout.findViewById(R.id.NameEditSave)
                 WorkoutName=editText.text.toString()
                 //construire la data à être enregistrer
                 val timer:Timer= Timer(0,WorkoutName,args.workTime,args.restTime,args.round)
                 model.insert(timer)
             }

             builder.setNegativeButton(android.R.string.no) { dialog, which ->
             }
            builder.setView(dialogLayout)

             builder.show()
         }

    }

    private fun populateUI(dialogLayout: View) {
        dialogLayout.findViewById<TextView>(R.id.RegisterWO_min_home_id).text =
            TwoDigitFormat?.format(retrieveMin(args.workTime)).toString()
        dialogLayout.findViewById<TextView>(R.id.RegisterWO_sec_home_id).text =
            TwoDigitFormat?.format(retrieveSeconde(args.workTime)).toString()

        dialogLayout.findViewById<TextView>(R.id.RegisterRE_min_home_id).text =
            TwoDigitFormat?.format(retrieveMin(args.restTime)).toString()
        dialogLayout.findViewById<TextView>(R.id.RegisterRE_sec_home_id).text =
            TwoDigitFormat?.format(retrieveSeconde(args.restTime)).toString()

        dialogLayout.findViewById<TextView>(R.id.RegisterWO_round_home_id).text =
            TwoDigitFormat?.format(retrieveSeconde(args.round)).toString()
    }

    /***
     * compute seconde and min
     */
    fun retrieveSeconde(Seconde:Int):Int{
        var ret:Int= 0
        ret = Seconde%60
        return ret
    }

    fun retrieveMin(Seconde:Int):Int{
        var ret:Int= 0
        ret = Seconde.div(60)
        return ret
    }
}