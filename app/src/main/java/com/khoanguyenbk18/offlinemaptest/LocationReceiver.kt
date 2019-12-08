package com.khoanguyenbk18.offlinemaptest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Parcelable
import android.util.Log
import br.com.safety.locationlistenerhelper.core.SettingsLocationTracker


class LocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (null != intent && intent.action == "my.action") {
            val locationData: Location =
                intent.getParcelableExtra<Parcelable>(SettingsLocationTracker.LOCATION_MESSAGE) as Location
            Log.d(
                "Location: ",
                "Latitude: " + locationData.latitude.toString() + "Longitude:" + locationData.getLongitude()
            )
            //send your call to api or do any things with the of location data
        }
    }
}