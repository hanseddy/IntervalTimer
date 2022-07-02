package com.example.interval_timer.UI

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.interval_timer.R


class FinishedWorkout : Fragment() {

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
       val button=view.findViewById<ImageButton>(R.id.register_wo_finishedWO)

          button.setOnClickListener {
             val builder = AlertDialog.Builder(context)
             builder.setTitle("Save the workout")
              val inflater:LayoutInflater=layoutInflater
              val dialogLayout=inflater.inflate(R.layout.popup_editname,null)
              val editText:EditText= dialogLayout.findViewById(R.id.NameEditSave)


             builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                 Toast.makeText(context,
                     android.R.string.yes, Toast.LENGTH_SHORT).show()
             }

             builder.setNegativeButton(android.R.string.no) { dialog, which ->
             }
            builder.setView(dialogLayout)

             builder.show()
         }

    }
}