package com.khoanguyenbk18.offlinemaptest

import android.Manifest
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_online_map_test.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint


class OnlineMapTest : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )



        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    Toast.makeText(this@OnlineMapTest, "Granted", Toast.LENGTH_SHORT).show()
                    initOnlineMap()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(this@OnlineMapTest, "Error ${it.name}", Toast.LENGTH_SHORT).show()
            }
            .onSameThread()
            .check()
    }

    private fun initOnlineMap() {
        setContentView(R.layout.activity_online_map_test)
        online_map.setTileSource(TileSourceFactory.MAPNIK)
    }

    override fun onResume() {
        super.onResume()
        online_map?.onResume()
    }

    override fun onPause() {
        super.onPause()
        online_map?.onPause()
    }

    private fun initDefaultViewPoint() {
        val mapController = online_map.controller
        mapController.setZoom(20.0)
        val startPoint = GeoPoint(10.7773162, 106.6543905)
        mapController.setCenter(startPoint)
    }



}
