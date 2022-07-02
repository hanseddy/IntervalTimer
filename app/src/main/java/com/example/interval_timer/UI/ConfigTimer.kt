package com.example.interval_timer.UI

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.interval_timer.R
import kotlin.properties.Delegates

class ConfigTimer : Fragment() {
    //enum
    enum class Selection {
        WORK, REST, ROUND
    }
    //picker
    lateinit var m_minutes_picker: NumberPicker
    lateinit var m_seconde_picker: NumberPicker
    lateinit var roundNumPicker: NumberPicker
    //linearLayout
    lateinit var workLayoutButton:LinearLayout
    lateinit var restLayoutButton:LinearLayout
    lateinit var roundLayoutButton:LinearLayout
    //selection
    var selection:Selection = Selection.WORK
    //temps configure: Work,rest,round
    var workMin :Int=0
    var worksec :Int=0
    var restMin :Int=0
    var restsec :Int=0
    var workTimeInSec by Delegates.notNull<Int>()


    var restTimeInSec by Delegates.notNull<Int>()

    var round by Delegates.notNull<Int>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialisation
        // Numpicker
        m_minutes_picker=view.findViewById(R.id.minutes_picker)
        m_seconde_picker=view.findViewById(R.id.seconde_picker)
        roundNumPicker=view.findViewById(R.id.round_data_picker)
        // layout selection
        workLayoutButton = view.findViewById(R.id.workout_selection)
        restLayoutButton = view.findViewById(R.id.rest_selection)
        roundLayoutButton = view.findViewById(R.id.round_selections)
        val button:Button= view.findViewById(R.id.ok_button_config)

        //initialise data
        NumPickerInit()

        button.setOnClickListener {
            sendTimeData(view)
        }
        SelectionManagement(view)

        // get data
        m_minutes_picker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
            //Toast.makeText(context,"picker"+picker.value.toString(),Toast.LENGTH_SHORT).show()
            if(selection==Selection.WORK){
                workMin=newVal
                view.findViewById<TextView>(R.id.work_data_minutes).text=newVal.toString()
            }else if(selection==Selection.REST) {
                restMin = newVal
                view.findViewById<TextView>(R.id.rest_data_minute).text=newVal.toString()
            }
        })
        m_seconde_picker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
            //Toast.makeText(context,"picker"+picker.value.toString(),Toast.LENGTH_SHORT).show()
            if(selection==Selection.WORK){
                worksec=newVal
                view.findViewById<TextView>(R.id.work_data_sec).text=newVal.toString()
            }else if(selection==Selection.REST) {
                restsec = newVal
                view.findViewById<TextView>(R.id.rest_data_sec).text=newVal.toString()
            }
        })
        roundNumPicker.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
            //Toast.makeText(context,"picker"+picker.value.toString(),Toast.LENGTH_SHORT).show()
            round = newVal
            view.findViewById<TextView>(R.id.round_data).text=newVal.toString()
        })

    }

    //initialisation method
    private fun NumPickerInit() {
        with(m_minutes_picker) {
            maxValue = 60
            minValue = 0
        }
        with(m_seconde_picker) {
            maxValue = 60
            minValue = 0
        }
        with(roundNumPicker) {
            maxValue = 100
            minValue = 0
        }
    }

    //send data
    private fun sendTimeData(view: View) {
        //récupérer les data: workout,rest et round
        workTimeInSec=concatMinSec(workMin,worksec)
        restTimeInSec=concatMinSec(restMin,restsec)
        //send data through Navigation
        if (workTimeInSec==0 || restTimeInSec==0){
            Toast.makeText(context,"please enter time",Toast.LENGTH_SHORT).show()
        }else{
            val action =ConfigTimerDirections.actionConfigTimerToHomeTimer().setWorktime(workTimeInSec).setRestTime(restTimeInSec).setRound(round)
            view.findNavController().navigate(action)
        }

    }

    // manage UI : show or hide element in the design
    fun SelectionManagement(view: View){
        // si c'est work qui est selectionner
        workLayoutButton.setOnClickListener {
            Log.i("workselected","work selection layout is clicked")
            view.findViewById<LinearLayout>(R.id.time_picker).visibility=View.VISIBLE
            view.findViewById<LinearLayout>(R.id.round_data_layout).visibility=View.GONE
            // change state
            selection=Selection.WORK
            //reset number picker
            initNumPickerToZero()
            //set title
            setTitle(view)
        }
        // si c'est rest qui est selectionner
        restLayoutButton.setOnClickListener {
            Log.i("restselected","rest selection layout is clicked")
            view.findViewById<LinearLayout>(R.id.time_picker).visibility=View.VISIBLE
            view.findViewById<LinearLayout>(R.id.round_data_layout).visibility=View.GONE
            // change state
            selection=Selection.REST
            //reset number picker
            initNumPickerToZero()
            //set title
            setTitle(view)
        }
        // si c'est round qui est selectionner
        roundLayoutButton.setOnClickListener {
            Log.i("roundselected","round selection layout is clicked")
            view.findViewById<LinearLayout>(R.id.time_picker).visibility=View.GONE
            view.findViewById<LinearLayout>(R.id.round_data_layout).visibility=View.VISIBLE
            // change state
            selection=Selection.ROUND
            //set title
            setTitle(view)
        }
    }

    private fun initNumPickerToZero() {
        m_minutes_picker.value = 0
        m_seconde_picker.value = 0
    }

    //methode qui concatene les minutes et les seconde => retourn du seconde
    fun concatMinSec(min:Int,sec:Int):Int{
        var concat:Int=0
        if (min==0){
            concat=sec
        } else if(min>0){
            concat= min.times(60).plus(sec)
        }
        return concat
    }

    fun setTitle(view: View){
        when(selection){
            Selection.WORK-> view.findViewById<TextView>(R.id.textView_Title).text= "WORK"
            Selection.REST-> view.findViewById<TextView>(R.id.textView_Title).text= "REST"
            Selection.ROUND-> view.findViewById<TextView>(R.id.textView_Title).text= "ROUND"
        }
    }
}