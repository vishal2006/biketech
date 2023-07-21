package com.example.biketech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_cvdashboard.*
import java.util.*

class EVDashboard : AppCompatActivity() {

    lateinit var evProgressBar: CircularProgressBar
    lateinit var evSpeedometer: TextView
    lateinit var BatteryProgressBar: CircularProgressBar
    lateinit var Batterymeter: TextView
    lateinit var evTime: TextView

    val c = Calendar.getInstance()
    var hour = c.get(Calendar.HOUR)
    val minute = c.get(Calendar.MINUTE)
    val state = c.get(Calendar.HOUR_OF_DAY)

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_evdashboard)

        evProgressBar = findViewById(R.id.EVProgressBar)
        evSpeedometer = findViewById(R.id.EVSpeedometer)
        BatteryProgressBar = findViewById(R.id.BatteryProgressBar)
        Batterymeter = findViewById(R.id.Batterymeter)
        evTime = findViewById(R.id.evtime)

        evProgressBar.apply {
            setProgressWithAnimation(75f, 1000)
            evSpeedometer.text = "100"
        }

        BatteryProgressBar.apply {
            setProgressWithAnimation(75f, 1000)
            Batterymeter.text = "100".plus("%")
        }

        settime()


    }

    private fun settime() {
        var timestate = "AM"
        var currentTime = "12.00"

        if (state>12){
            timestate = "PM"
        }
        if (minute<10){
            currentTime= (hour.toString()).plus(":").plus("0").plus(minute.toString()).plus(" ").plus(timestate)
        }
        else {
            currentTime = (hour.toString()).plus(":").plus(minute.toString()).plus(" ").plus(timestate)
        }
        evTime.text = currentTime

    }
}