package com.example.biketech

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*

class BluetoothDevices : AppCompatActivity() {

    var m_bluetoothAdapter: BluetoothAdapter? = null
    lateinit var m_pairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1
    lateinit var select_device_refresh: Button
    lateinit var select_device_list: ListView

    companion object{
        val EXTRA_ADDRESS: String = "Device_address"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_devices)

        select_device_refresh = findViewById(R.id.select_device_refresh)
        select_device_list = findViewById(R.id.select_device_list)

        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null){
            Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
        }

        if (!m_bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener { pairedDeviceList() }

    }

    private fun pairedDeviceList(){

        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<String> = ArrayList()

        if(!m_pairedDevices.isEmpty()){
            for (device: BluetoothDevice in m_pairedDevices){
                list.add(device.name.plus(" ").plus(device.address))
                Log.i("device",""+device)
            }
        }
        else{
            Toast.makeText(this, "No paired device found", Toast.LENGTH_LONG).show()
        }

        val adapter = ArrayAdapter(this,R.layout.row,list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _,position, _ ->
            val device: String = list[position]
            val address: String = device.takeLast(17)

            val intent = Intent(this,CVDashboard::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode == Activity.RESULT_OK){
                if(m_bluetoothAdapter!!.isEnabled){
                    Toast.makeText(this,"Bluetooth has been enabled",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this,"Bluetooth has been disabled",Toast.LENGTH_LONG).show()
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this,"Bluetooth enabling has been cancelled",Toast.LENGTH_LONG).show()
            }
        }
    }


}