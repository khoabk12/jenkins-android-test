package com.khoanguyenbk18.offlinemaptest

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.util.*

class GoogleService : Service(), LocationListener {

    companion object {
        val strReceiver = "servicetutorial.service.receiver"
    }

    private var isGPSEnable = false
    private var isNetworkEnable = false
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private lateinit var locationManager: LocationManager
    private var location: Location? = null
    private var mHandler = Handler()
    private var mTimer: Timer? = null
    private var notifyInterval = 1000L;
    private lateinit var intent: Intent

    override fun onCreate() {
        super.onCreate()
        mTimer = Timer()
        mTimer!!.schedule(TimerTaskToGetLocation(), 5L, notifyInterval)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

    inner class TimerTaskToGetLocation : TimerTask() {
        override fun run() {
            mHandler.post {
                fnGetLocation()
            }
        }
    }

    private fun fnUpdate(location: Location) {
        intent.putExtra("latitude", location.latitude)
        intent.putExtra("longitude", location.longitude)
        sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    private fun fnGetLocation() {
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnable && !isNetworkEnable) {

        } else {
            if (isNetworkEnable) {
                location = null;
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    0F,
                    this
                )
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    latitude = location!!.latitude
                    longitude= location!!.longitude
                    Log.e("lat", location!!.latitude.toString())
                    Log.e("lat", location!!.longitude.toString())
                    fnUpdate(location!!)
                }
            }

            if (isGPSEnable) {
                location = null;
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000L,
                    0F,
                    this
                )
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location!!.latitude
                    longitude= location!!.longitude
                    Log.e("lat", location!!.latitude.toString())
                    Log.e("lat", location!!.longitude.toString())
                    fnUpdate(location!!)
                }
            }
        }
    }
}

