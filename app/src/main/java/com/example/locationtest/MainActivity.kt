package com.example.locationtest

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
//import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
//import kotlin.random.Random

data class LapTimeItem(val lap: String, val lapTime: Long)

class MainActivity : AppCompatActivity() {
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 30).apply {
            setMinUpdateDistanceMeters(10F)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()
    private val maxLaps:Int=15
    private lateinit var tvRoundNo:TextView
    private lateinit var tvRoundDescription:TextView
    private lateinit var tvLaptime: TextView
    private lateinit var tvTimediff: TextView
    private lateinit var tvWaypoint: TextView
    private lateinit var tvSpeed: TextView
    private lateinit var tvTime: TextView
    private lateinit var rvLaps:RecyclerView
    private var time:Long = 0
    private var timeRemember = System.currentTimeMillis()
    private var lastWptIndex: Int = -1
    private var lapNo:Int=0
    private val confirmationLap:Int=9
    private lateinit var lapTimes:LongArray
    private lateinit var bestTimes:LongArray
    private lateinit var sectorTimes:Array<Array<Long>>
    private var lapTimeItems = mutableListOf<LapTimeItem>()
    private val scheduledExecutor = Executors.newSingleThreadScheduledExecutor()
    private val delayMillis = 100L // 0,1 second
    private val initialDelayMillis = 0L // Start the task immediately
    /*private val handler2 = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private data class Waypoint(
        val name: String,
        val latitude: Double,
        val longitude: Double,
    )
    private data class Testlocation(
        val latitude:Double,
        val longitude: Double,
        val speed:Double
    )
    private val waypoints = arrayOf(
        Waypoint("Vorstart",50.0836,8.54),
        Waypoint("Start/Ziel",51.08337,9.54105),
        Waypoint("Vorstart",50.0836,8.54),
        Waypoint("BiÖ1",51.08275,9.54072),
        Waypoint("Vorstart",50.0836,8.54),
        Waypoint("Kurve2",51.08239,9.54191),
        Waypoint("Vorstart",50.0836,8.54),
        Waypoint("Kurve3",51.08326,9.54332),
        Waypoint("Vorstart",50.0836,8.54),
        Waypoint("BiÖ11",51.08395,9.54159)
    )
    private var wptPosition=0
    private fun testCallback(location:Testlocation) {
        if (lapNo<=maxLaps) {
            tvSpeed.text = String.format("%.2f", (location.speed * 3.6))
            val wpt = Nordschleife.isWaypointReached(location.latitude, location.longitude)
            if (wpt.waypointIndex != lastWptIndex && wpt.waypointIndex != -1) {
                lastWptIndex = wpt.waypointIndex
                if (wpt.waypointIndex == 0) {
                    lapNo++
                    if (lapNo == 1) {
                        showSeconds(true)
                    } else {
                        time = System.currentTimeMillis() - timeRemember
                        lapTimes[lapNo - 2] = time
                        var newItem = LapTimeItem(getString(R.string.round, lapNo - 1), time)
                        lapTimeItems.add(newItem)
                        rvLaps.adapter!!.notifyItemInserted(lapTimeItems.size - 1)
                        rvLaps.scrollToPosition(lapTimeItems.size - 1)
                        if (lapNo==9) {
                            newItem= LapTimeItem(getString(R.string.r7_r8_combined),
                                lapTimes[6]+lapTimes[7])
                            lapTimeItems.add(newItem)
                            rvLaps.adapter!!.notifyItemInserted(lapTimeItems.size - 1)
                            rvLaps.scrollToPosition(lapTimeItems.size - 1)
                        } else if (lapNo==10) {
                            newItem= LapTimeItem(getString(R.string.diff_set_confirmation),
                                lapTimes[0]-lapTimes[8])
                            lapTimeItems.add(newItem)
                            rvLaps.adapter!!.notifyItemInserted(lapTimeItems.size - 1)
                            rvLaps.scrollToPosition(lapTimeItems.size - 1)
                        }
                    }
                    if (lapNo > maxLaps) {
                        tvRoundDescription.text = getString(R.string.end_of_race)
                        handler2.removeCallbacks(runnable!!)
                        showSeconds(false)
                        fusedLocationProvider?.removeLocationUpdates(locationCallback)
                        return
                    }
                    tvRoundNo.text = getString(R.string.round, lapNo)
                    tvRoundDescription.text = Nordschleife.getRoundName(lapNo)
                    timeRemember = System.currentTimeMillis()
                } else {
                    time = System.currentTimeMillis() - timeRemember
                }
                tvWaypoint.text = wpt.waypointName
                tvTime.text = formatMillisecondsToTime(time)
                sectorTimes[lapNo - 1][lastWptIndex] = time
                var timediff = time - bestTimes[lastWptIndex]
                if (lapNo == confirmationLap) {
                    timediff = time - sectorTimes[0][lastWptIndex]
                }
                if (time < bestTimes[lastWptIndex] || bestTimes[lastWptIndex] == 0L) bestTimes[lastWptIndex] =
                    time
                if (timediff <= 0) {
                    tvTimediff.setTextColor(getColor(R.color.green))
                } else {
                    tvTimediff.setTextColor(getColor(R.color.red))
                }
                if (lapNo == 1) {
                    tvTimediff.text = ""
                } else {
                    tvTimediff.text = String.format("%.2f", (timediff.toDouble() / 1000))
                }
            }
        }
    }*/
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                if (lapNo <= maxLaps) {
                    tvSpeed.text = String.format("%.2f", (location.speed * 3.6))
                    val wpt = Nordschleife.isWaypointReached(location.latitude, location.longitude)
                    if (wpt.waypointIndex != lastWptIndex && wpt.waypointIndex != -1) {
                        lastWptIndex = wpt.waypointIndex
                        if (wpt.waypointIndex == 0) {
                            lapNo++
                            if (lapNo == 1) {
                                showSeconds(true)
                            } else {
                                time = System.currentTimeMillis() - timeRemember
                                lapTimes[lapNo - 2] = time
                                var newItem =
                                    LapTimeItem(getString(R.string.round, lapNo - 1), time)
                                lapTimeItems.add(newItem)
                                rvLaps.adapter!!.notifyItemInserted(lapTimeItems.size - 1)
                                rvLaps.scrollToPosition(lapTimeItems.size - 1)
                                if (lapNo == 9) {
                                    newItem = LapTimeItem(
                                        getString(R.string.r7_r8_combined),
                                        lapTimes[6] + lapTimes[7]
                                    )
                                    lapTimeItems.add(newItem)
                                    rvLaps.adapter!!.notifyItemInserted(lapTimeItems.size - 1)
                                    rvLaps.scrollToPosition(lapTimeItems.size - 1)
                                } else if (lapNo == 10) {
                                    newItem = LapTimeItem(
                                        getString(R.string.diff_set_confirmation),
                                        lapTimes[0] - lapTimes[8]
                                    )
                                    lapTimeItems.add(newItem)
                                    rvLaps.adapter!!.notifyItemInserted(lapTimeItems.size - 1)
                                    rvLaps.scrollToPosition(lapTimeItems.size - 1)
                                }
                            }
                            if (lapNo > maxLaps) {
                                tvRoundDescription.text = getString(R.string.end_of_race)
                                //handler2.removeCallbacks(runnable!!)
                                showSeconds(false)
                                stopLocationUpdates()
                                return
                            }
                            tvRoundNo.text = getString(R.string.round, lapNo)
                            tvRoundDescription.text = Nordschleife.getRoundName(lapNo)
                            timeRemember = System.currentTimeMillis()
                        } else {
                            time = System.currentTimeMillis() - timeRemember
                        }
                        tvWaypoint.text = wpt.waypointName
                        tvTime.text = formatMillisecondsToTime(time)
                        sectorTimes[lapNo - 1][lastWptIndex] = time
                        var timediff = time - bestTimes[lastWptIndex]
                        if (lapNo == confirmationLap) {
                            timediff = time - sectorTimes[0][lastWptIndex]
                        }
                        if (time < bestTimes[lastWptIndex] || bestTimes[lastWptIndex] == 0L) bestTimes[lastWptIndex] =
                            time
                        if (timediff <= 0) {
                            tvTimediff.setTextColor(getColor(R.color.green))
                        } else {
                            tvTimediff.setTextColor(getColor(R.color.red))
                        }
                        if (lapNo == 1) {
                            tvTimediff.text = ""
                        } else {
                            tvTimediff.text = String.format("%.2f", (timediff.toDouble() / 1000))
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        lapTimes= LongArray(maxLaps)
        sectorTimes= Array(maxLaps) { Array(Nordschleife.getSectorCount()) {0} }
        tvRoundNo=findViewById(R.id.tv_round_no)
        tvRoundDescription=findViewById(R.id.tv_round_description)
        tvLaptime = findViewById(R.id.tv_laptime)
        tvTimediff = findViewById(R.id.tv_timediff)
        tvWaypoint = findViewById(R.id.tv_waypoint)
        tvSpeed = findViewById(R.id.tv_speed)
        tvTime = findViewById(R.id.tv_time)
        tvWaypoint.text=getString(R.string.prestart)
        rvLaps=findViewById(R.id.rv_rundenliste)
        rvLaps.layoutManager=LinearLayoutManager(this)
        bestTimes= LongArray(Nordschleife.getSectorCount()) {0L}
        val adapter = ItemAdapter(lapTimeItems)
        rvLaps.adapter = adapter
        rvLaps.layoutManager = LinearLayoutManager(this)
        /*runnable = object : Runnable {
            override fun run() {
                val waypoint = waypoints[wptPosition++]
                if (wptPosition > waypoints.size - 1) wptPosition = 0
                val random = Random.Default
                val location = Testlocation(waypoint.latitude, waypoint.longitude, random.nextDouble(5.0, 50.0))
                testCallback(location)
                handler2.postDelayed(this, random.nextLong(800, 1200)) // Schedule the task again after the delay
            }
        }

        handler2.postDelayed(runnable as Runnable, 1000)*/
    }
    private fun showSeconds(doShow:Boolean) {
        if (doShow) {
            scheduledExecutor.scheduleAtFixedRate({
                runOnUiThread {
                    tvLaptime.text =
                        formatMillisecondsToTime(System.currentTimeMillis() - timeRemember)
                }
            }, initialDelayMillis, delayMillis, TimeUnit.MILLISECONDS)
        } else {
            scheduledExecutor.shutdown()
        }
    }
    private fun stopLocationUpdates() {
        fusedLocationProvider?.removeLocationUpdates(locationCallback)
    }
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        //if (lapNo>0) showSeconds()
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.removeLocationUpdates(locationCallback)
        }
        //scheduledExecutor.shutdown()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", this.packageName, null),
                            ),
                        )
                    }
                }
                return
            }

            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationProvider?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        Toast.makeText(
                            this,
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }
    @Suppress("UNUSED_PARAMETER")
    fun gotoStartpage(v:View) {
        stopLocationUpdates()
        showSeconds(false)
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }
    @Suppress("UNUSED_PARAMETER")
    fun restart(v:View) {
        lapTimes= LongArray(maxLaps)
        sectorTimes= Array(maxLaps) { Array(Nordschleife.getSectorCount()) {0} }
        lapNo=0
        showSeconds(false)
        lastWptIndex=-1
        rvLaps.adapter?.apply {
            val itemCount = itemCount // Get the current item count
            if (itemCount > 0) {
                lapTimeItems.clear() // Clear the data list
                notifyItemRangeRemoved(0, itemCount) // Notify the adapter that items are removed
            }
        }
        setContentView(R.layout.activity_main)
    }
    fun formatMillisecondsToTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        //val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val millis = (milliseconds % 1000) / 10
        return String.format("%02d:%02d.%02d",  minutes, seconds, millis)
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }
}