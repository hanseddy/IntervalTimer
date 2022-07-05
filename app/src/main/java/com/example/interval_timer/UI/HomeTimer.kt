package com.example.interval_timer.UI

import android.graphics.Typeface
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.media.MediaPlayer
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
import com.example.interval_timer.database.Timer
import com.example.interval_timer.database.TimerDatabase
import com.example.interval_timer.repository.TimerRepository
import com.example.interval_timer.viewmodel.TimerViewmodel

class HomeTimer : Fragment() {

    /**
     * declaration
     */
    // countdown timer
    lateinit var m_startTimer: CountDownTimer
    lateinit var m_restTimer: CountDownTimer
    lateinit var m_pausedAfterworkTimer: CountDownTimer

    //mode
    enum class Mode{
        PLAY,PAUSE
    }
    //state
    enum class Etat{
        WORK,REST
    }
    var mode:Mode= Mode.PLAY
    var m_woTime=0
    var m_restTime =0
    var m_round =0

    var m_round_finished=0

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

    var beforePaused:Boolean=false
    lateinit var workMediaPlayer:MediaPlayer
    lateinit var restMediaPlayer:MediaPlayer
    /**
     * end of declaration
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mediaplayer
        workMediaPlayer = MediaPlayer.create(context, R.raw.clock_tick)
        restMediaPlayer = MediaPlayer.create(context, R.raw.clock_tick2)
        //formatage 2 digit
        TwoDigitFormat = DecimalFormat("00")
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
            m_round_finished= args.round
        Log.i("time","woTime"+m_woTime+" rest"+m_restTime+ "m_round" +m_round)
        /***
         * set connfig data in viewmodel
         */

        model.setconfigworkTime(m_woTime)
        model.setconfigrestTime(m_restTime)
        model.setconfigroundTime(m_round)

        /***
         * populate last workout
         */
        //populateLastWorkout(view,model)
        model.configworkTime.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.LastWO_min_home).text=TwoDigitFormat?.format(retrieveMin(it)).toString()
            view.findViewById<TextView>(R.id.LastWO_sec_home).text=TwoDigitFormat?.format(retrieveSeconde(it)).toString()
        })

        model.configrestTime.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.LastRest_min_home).text=TwoDigitFormat?.format(retrieveMin(it)).toString()
            view.findViewById<TextView>(R.id.LastRest_sec_home).text=TwoDigitFormat?.format(retrieveSeconde(it)).toString()
        })
        model.configroundTime.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.LastWO_round_home).text=it.toString()
        })

        /****
         * button management
         */
        //new workout
        new_wo_button.setOnClickListener {
            navController.navigate(R.id.configTimer)
        }

        //play button
        view.findViewById<ImageButton>(R.id.play_pause_button).setOnClickListener {
            PlayPauseManagButton(view)
        }


        //Nav to register workout
        view.findViewById<ImageButton>(R.id.save_button_home).setOnClickListener {
            navController.navigate(R.id.registerWorkout)
        }
        /**
         * populate main UI
         */
        // initialisation de format
        min_layout.text= TwoDigitFormat?.format(retrieveMin(m_woTime)).toString()//retrieveMin(m_woTime).toString()  //String.format("%02d", (retrieveMin(m_woTime).toString()))//
        sec_layout.text= TwoDigitFormat?.format(retrieveSeconde(m_woTime)).toString()
        round_layout.text=m_round.toString()



    }

    private fun PlayPauseManagButton(view: View) {
        var l_round: Int = 0
        var l_min: Int = 0
        var l_sec: Int = 0

        if (mode == Mode.PLAY) {

            mode = Mode.PAUSE
            playButtonManagingIcon(view)

            if (beforePaused == false) {
                //start interval timer
                StartWorkout(navController, (m_woTime.plus(1)), (m_restTime.plus(1)), m_round, view)

            } else if (beforePaused == true) {
                if (state == Etat.WORK) {
                    // appel les fonctions pour d'abord countdown du left work puis rest
                    val woTimeleft: Int = model.getworkTimeLeft()!!
                    CountdownWorkAfterPaused(
                        navController,
                        (m_woTime.plus(1)),
                        (woTimeleft.plus(1)),
                        (m_restTime.plus(1)),
                        view
                    )
                } else if (state == Etat.REST) {
                    // appel rest puis on revient au normal
                    val restTimeleft: Int = model.getrestTimeLeft()

                    CountdownRestAfterPaused(
                        navController,
                        (m_woTime.plus(1)),
                        (restTimeleft.plus(1)),
                        view
                    )
                }
            }
        } else if (mode == Mode.PAUSE) {

            mode = Mode.PLAY
            playButtonManagingIcon(view)

            if (state == Etat.WORK) {
                stoppedClockManagWork(l_round, l_min, l_sec)
            } else if (state == Etat.REST) {
                stopedClockManagRest(l_min, l_sec, l_round)
            }
            beforePaused = true
        }
    }

    private fun stoppedClockManagWork(l_round: Int, l_min: Int, l_sec: Int) {
        // arreter le countdown
        var l_round1 = l_round
        var l_min1 = l_min
        var l_sec1 = l_sec
        m_startTimer.cancel()
        // rafraichir data: numRoundLeft, workTimeLeft if we are in work state
        l_round1 = Integer.parseInt(round_layout.text.toString())
        l_min1 = Integer.parseInt(min_layout.text.toString())
        l_sec1 = Integer.parseInt(sec_layout.text.toString())
        val l_worktimeleft: Int = l_min1.times(60).plus(l_sec1)

        model.setnumRoundLeft(l_round1) // changer le round
        model.setworkTimeLeft(l_worktimeleft)
        // freeze UI
        model.workTimeLeft.observe(viewLifecycleOwner, Observer {
            min_layout.text = TwoDigitFormat.format(retrieveMin(it)).toString()
            sec_layout.text = TwoDigitFormat.format(retrieveSeconde(it)).toString()
        })
        model.numRoundLeft.observe(viewLifecycleOwner, Observer {
            round_layout.text = it.toString()
        })
    }

    private fun stopedClockManagRest(l_min: Int, l_sec: Int, l_round: Int) {
        // arreter le countdown
        var l_min1 = l_min
        var l_sec1 = l_sec
        var l_round1 = l_round
        m_restTimer.cancel()
       // m_pausedAfterworkTimer.cancel()  //arreter si hold in rest workafterpaused
        // freeze UI
        l_round1 = Integer.parseInt(round_layout.text.toString())
        l_min1 = Integer.parseInt(min_layout.text.toString())
        l_sec1 = Integer.parseInt(sec_layout.text.toString())

        val l_restTimeleft: Int = l_min1.times(60).plus(l_sec1)

        model.setnumRoundLeft(l_round1) // changer le round
        model.setrestTimeLeft(l_restTimeleft)

        // rafraichir data: numRoundLeft, restTimeLeft if we are in rest state
        model.restTimeLeft.observe(viewLifecycleOwner, Observer {
            min_layout.text = TwoDigitFormat.format(retrieveMin(it)).toString()
            sec_layout.text = TwoDigitFormat.format(retrieveSeconde(it)).toString()
        })
        model.numRoundLeft.observe(viewLifecycleOwner, Observer {
            round_layout.text = it.toString()
        })
    }

    /**
     *  function : countdown   status => not finished yet
     */
    fun StartWorkout(navController: NavController,woTime:Int, restTime:Int, round:Int,view: View){
        // faire le décompte
        m_startTimer =object : CountDownTimer((woTime*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setUI_W_R_state(state, view)
                round_layout.text=m_round.toString() //set round UI
                var min =retrieveMin((millisUntilFinished/1000).toInt())
                var sec =retrieveSeconde((millisUntilFinished/1000).toInt())
                //format
                min_layout.text= TwoDigitFormat?.format(min).toString()
                sec_layout.text= TwoDigitFormat?.format(sec).toString()
                // audio sound
                workMediaPlayer.start()
            }
            override fun onFinish() {
                //start rest countdown
                countdownrest(navController,woTime,restTime,m_round,view)
                // set state to REST
                state=Etat.REST
            }
        }.start()
    }

    fun countdownrest(navController: NavController,woTime:Int,restTime: Int,round: Int,view: View){
        m_restTimer = object : CountDownTimer((restTime*1000).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                setUI_W_R_state(state, view)
                var min =retrieveMin((millisUntilFinished/1000).toInt())
                var sec =retrieveSeconde((millisUntilFinished/1000).toInt())
                //format
                min_layout.text= TwoDigitFormat?.format(min).toString()
                sec_layout.text= TwoDigitFormat?.format(sec).toString()
                // audio sound
                restMediaPlayer.start()
            }
            override fun onFinish() {
                // decrement round
                m_round=m_round.minus(1)
                state=Etat.WORK
                //TODO: start work countdown
                if(m_round>0){
                    beforePaused=false
                    StartWorkout(navController,woTime,restTime,round,view)
                }else{
                    // when round =0 => navigate to finished
                    val action= HomeTimerDirections.actionHomeTimerToFinishedWorkout().setWorkTime(m_woTime).setRestTime(m_restTime).setRound(m_round_finished)
                    view.findNavController().navigate(action)
                //navController.navigate(R.id.finishedWorkout)
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
    fun populateLastWorkout(view: View,model:TimerViewmodel){
        model.configworkTime.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.LastWO_min_home).text=retrieveMin(it).toString()
            view.findViewById<TextView>(R.id.LastWO_sec_home).text=retrieveMin(it).toString()
        })

        model.configrestTime.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.LastRest_min_home).text=retrieveMin(it).toString()
            view.findViewById<TextView>(R.id.LastRest_sec_home).text=retrieveMin(it).toString()
        })
        model.configroundTime.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.LastWO_round_home).text=it.toString()
        })

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

            view.findViewById<TextView>(R.id.rest_status).typeface= Typeface.DEFAULT // set to normal
            view.findViewById<TextView>(R.id.rest_status).setTextColor(resources.getColor(R.color.black)) //set back the color

        }else if (state==Etat.REST){
            view.findViewById<TextView>(R.id.rest_status).typeface= Typeface.DEFAULT_BOLD // set to bold
            view.findViewById<TextView>(R.id.rest_status).setTextColor(resources.getColor(R.color.green)) //set color

            view.findViewById<TextView>(R.id.work_status_home).typeface= Typeface.DEFAULT // set to bold
            view.findViewById<TextView>(R.id.work_status_home).setTextColor(resources.getColor(R.color.black)) //set color
        }
    }

    /**
     *  Countdown if paused at second cycle: rest
     *  le paramettre : restTime ici est le rest qui n'a pas été decompté du à la mise en pause
     *  le paramettre : StarWorkout() m_round_paused doit être rafraichis  TODO
     */
    fun CountdownRestAfterPaused(navController: NavController,workTime:Int,restTime:Int,view: View){
        // faire le décompte
        object : CountDownTimer((restTime*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setUI_W_R_state(state, view)
                round_layout.text=m_round.toString() //set round UI
                var min =retrieveMin((millisUntilFinished/1000).toInt())
                var sec =retrieveSeconde((millisUntilFinished/1000).toInt())
                //format
                min_layout.text= TwoDigitFormat?.format(min).toString()
                sec_layout.text= TwoDigitFormat?.format(sec).toString()
                // audio sound
                restMediaPlayer.start()
            }
            override fun onFinish() {
                m_round=m_round.minus(1)
                // les parametre workTime reste le même
                StartWorkout(navController,workTime,m_restTime,m_round,view)
                // set state to REST
                state=Etat.WORK
            }
        }.start()
    }
    /**
     *  Countdown if paused at second cycle: work
     *  le paramettre : restTime ici est le rest qui n'a pas été decompté du à la mise en pause
     *  le paramettre : StarWorkout() m_round_paused doit être rafraichis
     */

    fun CountdownWorkAfterPaused(navController: NavController,woTime:Int,leftwoTime:Int, restTime:Int,view: View){
        // faire le décompte
        m_startTimer =object : CountDownTimer((leftwoTime*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // audio sound
                workMediaPlayer.start()
                setUI_W_R_state(state, view)
                round_layout.text=m_round.toString() //set round UI
                var min =retrieveMin((millisUntilFinished/1000).toInt())
                var sec =retrieveSeconde((millisUntilFinished/1000).toInt())
                //format
                min_layout.text= TwoDigitFormat?.format(min).toString()
                sec_layout.text= TwoDigitFormat?.format(sec).toString()

            }
            override fun onFinish() {
                // set state to REST*/
                state=Etat.REST
                countdownrest(navController,woTime,restTime,m_round,view)
            }
        }.start()
    }
}