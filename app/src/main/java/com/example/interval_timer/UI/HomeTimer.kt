package com.example.interval_timer.UI

import android.graphics.Typeface
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.interval_timer.R
import com.example.interval_timer.database.TimerDatabase
import com.example.interval_timer.repository.TimerRepository
import com.example.interval_timer.viewmodel.TimerViewmodel

class HomeTimer : Fragment() {

    //mode
    enum class Mode{
        PLAY,PAUSE
    }
    //state
    enum class Etat{
        WORK,REST
    }
    val mode:Mode= Mode.PLAY
    var m_woTime=0 //by Delegates.notNull<Int>()
    var m_restTime =0 //by Delegates.notNull<Int>()
    var m_round =0 //by Delegates.notNull<Int>()

    var state:Etat=Etat.WORK

    lateinit var min_layout:TextView
    lateinit var sec_layout:TextView
    lateinit var round_layout:TextView

    val args:HomeTimerArgs by navArgs()
    //button init
    lateinit var new_wo_button:ImageButton
    //viewmodel
    lateinit var model: TimerViewmodel
    //database
    lateinit var database:TimerDatabase
    //navigation component
    lateinit var navController:NavController
    //two digit format
    lateinit var TwoDigitFormat: NumberFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Database instanciation
        database= TimerDatabase.getDatabase(requireContext())
        // Repository
        val repository:TimerRepository= TimerRepository(database.timerDao())
        //viewmodel instantiation
        model=ViewModelProvider(this,TimerViewmodel.WordViewmodelFactory(repository))[TimerViewmodel::class.java]
        //navigation
        navController=view.findNavController()
        //initialisation
        min_layout = view.findViewById(R.id.minDecompte_home)
        sec_layout = view.findViewById(R.id.secDecompte_home)
        round_layout = view.findViewById(R.id.round_Home)
        new_wo_button=view.findViewById(R.id.new_wo_button_home)

        // get data sent by the config fragment
            m_woTime= args.worktime
            m_restTime= args.restTime
            m_round= args.round
        Log.i("time","woTime"+m_woTime+" rest"+m_restTime+ "m_round" +m_round)

        /****
         * button management
         */
        //new workout
        new_wo_button.setOnClickListener {
            navController.navigate(R.id.configTimer)
        }
        //play button
        view.findViewById<ImageButton>(R.id.play_pause_button).setOnClickListener {
            StartWorkout(navController,model,m_woTime,m_restTime,m_round,view)
        }
        //Nav to register workout
        view.findViewById<ImageButton>(R.id.save_button_home).setOnClickListener {
            navController.navigate(R.id.registerWorkout)
        }

        /**
         * populate main UI
         */
        // initialisation de format
        TwoDigitFormat = DecimalFormat("00")
        min_layout.text= TwoDigitFormat?.format(retrieveMin(m_woTime)).toString()//retrieveMin(m_woTime).toString()  //String.format("%02d", (retrieveMin(m_woTime).toString()))//
        sec_layout.text= TwoDigitFormat?.format(retrieveSeconde(m_woTime)).toString()
        round_layout.text=m_round.toString()

        /***
         * populate last workout
         */
        populateLastWorkout(view,m_woTime,m_restTime,m_round)
        /***
         * set ui countdown
         */
        // observe les livedata
        model.workRealTime.observe(viewLifecycleOwner, Observer {
            min_layout.text=retrieveMin(it).toString()
            sec_layout.text=retrieveSeconde(it).toString()
        })
        model.restRealTime.observe(viewLifecycleOwner, Observer {
            min_layout.text=retrieveMin(it).toString()
            sec_layout.text=retrieveSeconde(it).toString()
        })
        model.roundRealTime.observe(viewLifecycleOwner, Observer {
            round_layout.text=it.toString()
        })
        /**
         * format text
         */
        //String.format("02%d",)
    }

    /**
     *  function : countdown   status => not finished yet
     */
    fun StartWorkout(navController: NavController,model: ViewModel,woTime:Int, restTime:Int, round:Int,view: View){
        // faire le décompte
        object : CountDownTimer((woTime*1000).plus(1).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setUI_W_R_state(state, view)
                round_layout.text=m_round.toString()
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000)
                var min =retrieveMin((millisUntilFinished/1000).toInt())
                var sec =retrieveSeconde((millisUntilFinished/1000).toInt())
                //format
                min_layout.text= TwoDigitFormat?.format(min).toString()
                sec_layout.text= TwoDigitFormat?.format(sec).toString()
                //TODO: set data in viewmodel (ui is refreshed by viewmodel)

            }
            override fun onFinish() {
                Toast.makeText(context,"work "+m_round.toString(),Toast.LENGTH_SHORT).show()
                //start rest countdown
                countdownrest(navController,model,woTime,restTime,m_round,view)
                // set state to REST
                state=Etat.REST
            }
        }.start()
    }

    fun countdownrest(navController: NavController,model: ViewModel,woTime:Int,restTime: Int,round: Int,view: View){
        object : CountDownTimer((restTime*1000).plus(1).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                setUI_W_R_state(state, view)
                var min =retrieveMin((millisUntilFinished/1000).toInt())
                var sec =retrieveSeconde((millisUntilFinished/1000).toInt())
                //format
                min_layout.text= TwoDigitFormat?.format(min).toString()
                sec_layout.text= TwoDigitFormat?.format(sec).toString()
                //TODO: set data in viewmodel (ui is refreshed by viewmodel)

            }
            override fun onFinish() {
                // decrement round
                m_round=m_round.minus(1)
                state=Etat.WORK
                //TODO: start work countdown
                if(m_round>0){
                    Toast.makeText(context,"rest "+m_round.toString(),Toast.LENGTH_SHORT).show()
                    StartWorkout(navController,model,woTime,restTime,round,view)
                }else{
                    // when round =0 => navigate to finished
                    navController.navigate(R.id.finishedWorkout)
                }

            }
        }.start()
    }

    /****
     * Play,Pause data managing
     */
    fun playButtonManagingIcon(view: View){
        when(mode){
            Mode.PLAY -> view.findViewById<ImageButton>(R.id.play_pause_button).setImageResource(R.drawable.play_button)
            Mode.PAUSE ->view.findViewById<ImageButton>(R.id.play_pause_button).setImageResource(R.drawable.pause_button)
        }
    }

    /***
     * populate the data from the config data
     */
    fun populateLastWorkout(view: View,work:Int,rest:Int,round: Int){
        //work
        view.findViewById<TextView>(R.id.LastWO_min_home).text=retrieveMin(work).toString()
        view.findViewById<TextView>(R.id.LastWO_sec_home).text=retrieveMin(work).toString()
        //rest
        view.findViewById<TextView>(R.id.LastRest_min_home).text=retrieveMin(rest).toString()
        view.findViewById<TextView>(R.id.LastRest_sec_home).text=retrieveMin(rest).toString()
        //round
        view.findViewById<TextView>(R.id.LastWO_round_home).text=round.toString()
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
    /**
     * register in last workout in viewmodel
     */
    fun LastWoToViewmodel(work:Int,rest:Int,round:Int){

    }
    /**
     * set the state of the workout : either in work or in rest
     */
    fun setUI_W_R_state(state:Etat, view: View){
        if (state==Etat.WORK){
            view.findViewById<TextView>(R.id.work_status_home).typeface= Typeface.DEFAULT_BOLD // set to bold
            view.findViewById<TextView>(R.id.work_status_home).setTextColor(resources.getColor(R.color.green)) //set color
            //view.findViewById<TextView>(R.id.work_status_home).textSize=resources.getDimension(R.dimen.DominantstateSize)

            view.findViewById<TextView>(R.id.rest_status).typeface= Typeface.DEFAULT // set to normal
            view.findViewById<TextView>(R.id.rest_status).setTextColor(resources.getColor(R.color.black)) //set back the color
            //view.findViewById<TextView>(R.id.rest_status).textSize=resources.getDimension(R.dimen.RecessifstateSize) //set back the color
        }else if (state==Etat.REST){
            view.findViewById<TextView>(R.id.rest_status).typeface= Typeface.DEFAULT_BOLD // set to bold
            view.findViewById<TextView>(R.id.rest_status).setTextColor(resources.getColor(R.color.green)) //set color
            //view.findViewById<TextView>(R.id.rest_status).textSize=resources.getDimension(R.dimen.DominantstateSize) //set back the color

            view.findViewById<TextView>(R.id.work_status_home).typeface= Typeface.DEFAULT // set to bold
            view.findViewById<TextView>(R.id.work_status_home).setTextColor(resources.getColor(R.color.black)) //set color
            //view.findViewById<TextView>(R.id.work_status_home).textSize=resources.getDimension(R.dimen.RecessifstateSize)
        }
    }


}