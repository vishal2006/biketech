package com.example.biketech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.delay
import java.util.*
import kotlin.concurrent.timerTask

class Animation : AppCompatActivity() {

    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        val intent = Intent(this,MainActivity::class.java)
        timer = Timer()
        timer.schedule(timerTask {
                  startActivity(intent)
        },5000)


    }


}