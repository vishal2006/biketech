package com.example.biketech

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.android.synthetic.main.activity_cvdashboard.*
import java.io.IOException
import java.util.*

class CVDashboard : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    //BT
    lateinit var text:TextView
    //HC05 00:20:12:08:CB:27
    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String

        //loc
        private const val INTERVAL = (1000 * 2).toLong()
        private const val FASTEST_INTERVAL = (1000 * 1).toLong()
        //loc
    }
    //BT

    lateinit var cvProgressBar: CircularProgressBar
    lateinit var fuelProgressBar: CircularProgressBar
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    lateinit var cvSpeedometer: TextView
    lateinit var fuelMeter: TextView
    lateinit var time: TextView

    var PERMISSION_ID = 44
    var p1 = 0f
    var p2 = 0f
    var p3 = 0f
    var p4 = 0f


    lateinit var cvtoggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cvdashboard)

        //BT
        m_address = intent.getStringExtra(BluetoothDevices.EXTRA_ADDRESS).toString()
        ConnectToDevice(this).execute()
        //BT

        cvProgressBar = findViewById(R.id.CVProgressBar)
        fuelProgressBar = findViewById(R.id.FuelProgressBar)
        cvSpeedometer = findViewById(R.id.CVSpeedometer)
        fuelMeter = findViewById(R.id.FuelMeter)
        time = findViewById(R.id.time)
        val radioGroup = findViewById<RadioGroup>(R.id.cvtoggle)
        val fuelImage = findViewById<ImageView>(R.id.Batteryimage)

        val drawerLayout: DrawerLayout = findViewById(R.id.cvDrawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)
        cvtoggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(cvtoggle)
        cvtoggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val profileIntent = Intent(this,Profile::class.java)
        val documentsIntent = Intent(this,ImageSlideShow::class.java)
        val aboutIntent = Intent(this,About::class.java)
        val analyticsIntent = Intent(this,Analytics::class.java)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.profile-> startActivity(profileIntent)
                R.id.analytics-> startActivity(analyticsIntent)
                R.id.documents-> startActivity(documentsIntent)
                R.id.about-> startActivity(aboutIntent)
                R.id.signout->Sign_out_CV()
            }
            true
        }

        radioGroup.setOnCheckedChangeListener { group, checkedID ->
            if (checkedID == R.id.poweron){
                sendCommand("1")
            }
            else {
                sendCommand("5")
                receive()
            }
        }

        fuelImage.setOnClickListener {
            receive()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        lastLocation


    }

    private fun Sign_out_CV(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
        googleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                disconnect()
                val signoutIntent = Intent(this,SignInActivity::class.java)
                auth.signOut()
                startActivity(signoutIntent)
            })
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        if (cvtoggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun settime() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val state = c.get(Calendar.HOUR_OF_DAY)

        var timestate = "AM"
        var currentTime = "12.00"

        if (state>12){
            timestate = "PM"
        }
        currentTime = if (minute<10){
            (hour.toString()).plus(":").plus("0").plus(minute.toString()).plus(" ").plus(timestate)
        } else {
            (hour.toString()).plus(":").plus(minute.toString()).plus(" ").plus(timestate)
        }
        time.text = currentTime

    }

    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        get() {
            if (checkPermissions()) {
                if (isLocationEnabled) {
                    mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                        val location = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            p1 = location.longitude.toFloat()
                            p2 = location.latitude.toFloat()
                            val dSpeed = location.speed.toDouble()
                            val a = 3.6 * dSpeed
                            val kmhSpeed = Math.round(a).toInt()
                            settime()
                            cvSpeedometer.text = kmhSpeed.toString()
                            cvProgressBar.apply {
                                setProgressWithAnimation((((kmhSpeed*75)/220).toFloat()), 1000) // =1s

                            }

                            requestNewLocationData()

                        }
                    }
                } else {
                    Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG)
                        .show()
                }
            } else {
                requestPermissions()
            }
        }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Looper.myLooper()?.let {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback,
                it
            )
        }
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            p3 = mLastLocation.longitude.toFloat()
            p4 = mLastLocation.latitude.toFloat()
            val dSpeed = mLastLocation.speed.toDouble()
            val a = 3.6 * dSpeed
            val kmhSpeed = Math.round(a).toInt()
            cvProgressBar.apply {
                setProgressWithAnimation((((kmhSpeed*75)/220).toFloat()), 1000) // =1s

            }
            lastLocation
            cvmap.setOnClickListener {
                val mapIntent: Intent = Intent(Intent.ACTION_VIEW)
                mapIntent.data = Uri.parse("geo:".plus(p4.toString()).plus(",").plus(p3.toString()))
                val chooser: Intent = Intent.createChooser(mapIntent,"Launch Maps")
                startActivity(chooser)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }



    private val isLocationEnabled: Boolean
        get() {
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lastLocation
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            lastLocation
        }
    }



    //BT
    fun receive() {
        try {
            m_bluetoothSocket!!.outputStream.write("2".toByteArray())
            val receivedValue: Int = m_bluetoothSocket!!.inputStream.read()
            Log.d("string", receivedValue.toString())


            fuelProgressBar.apply {
                setProgressWithAnimation(receivedValue.toFloat(), 1000)
                fuelMeter.text = (receivedValue.toString()).plus("%")
            }
        }catch (e: IOException){
            e.printStackTrace()
        }



    }


    private fun sendCommand(input: String){

        if(m_bluetoothSocket!=null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun disconnect(){

        if (m_bluetoothSocket!=null){

            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>(){


        private var connectSuccess: Boolean = true
        @SuppressLint("StaticFieldLeak")
        private val context: Context = c


        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"connecting...","please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(m_bluetoothSocket == null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                }

            }catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data","couldn't connect")
            }
            else{
                m_isConnected = true


            }
            m_progress.dismiss()
        }
    }
    //BT
}