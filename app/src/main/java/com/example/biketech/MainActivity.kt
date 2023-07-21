package com.example.biketech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        motorBikeButton.setOnClickListener{
            val BluetoothDevicesIntent = Intent(this, BluetoothDevices::class.java)
            startActivity(BluetoothDevicesIntent)
        }

        electricBikeButton.setOnClickListener{
            val EVDashboardIntent = Intent(this, EVDashboard::class.java)
            startActivity(EVDashboardIntent)
        }

    }
}