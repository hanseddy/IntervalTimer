package com.example.interval_timer.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.interval_timer.R
import com.example.interval_timer.database.Timer

class TimerAdapter(val TimerList:ArrayList<Timer>): RecyclerView.Adapter<TimerAdapter.ViewHolder>()  {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val WOname:TextView
        val workMin: TextView
        val workSec: TextView
        val restMin: TextView
        val restSec: TextView
        val round: TextView

        init {
            // Define click listener for the ViewHolder's View.
            WOname = view.findViewById(R.id.workout_name)
            workMin = view.findViewById(R.id.RegisterWO_min_home_id)
            workSec = view.findViewById(R.id.RegisterWO_sec_home_id)
            restMin= view.findViewById(R.id.RegisterRE_min_home_id)
            restSec = view.findViewById(R.id.RegisterRE_sec_home_id)
            round = view.findViewById(R.id.RegisterWO_round_home_id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.timer_recycler_view_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.WOname.text=TimerList[position].WorkoutName
        holder.workMin.text=retrieveMin(TimerList[position].WorkoutTime).toString()
        holder.workSec.text=retrieveSeconde(TimerList[position].WorkoutTime).toString()
        holder.restMin.text=retrieveMin(TimerList[position].RestTime).toString()
        holder.restSec.text= retrieveSeconde(TimerList[position].RestTime).toString()
        holder.round.text=(TimerList[position].round).toString()
    }

    override fun getItemCount(): Int = TimerList.size
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