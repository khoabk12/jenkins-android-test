package com.khoanguyenbk18.offlinemaptest

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.safety.locationlistenerhelper.core.CurrentLocationListener
import br.com.safety.locationlistenerhelper.core.CurrentLocationReceiver
import br.com.safety.locationlistenerhelper.core.LocationTracker
import egolabsapps.basicodemine.offlinemap.Interfaces.GeoPointListener
import egolabsapps.basicodemine.offlinemap.Interfaces.MapListener
import egolabsapps.basicodemine.offlinemap.Utils.MapUtils
import egolabsapps.basicodemine.offlinemap.Views.OfflineMapView
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URI


class MainActivity : AppCompatActivity(), MapListener, GeoPointListener {

    private lateinit var offlineMapView: OfflineMapView

    private lateinit var locationTracker: LocationTracker

    //parse kml file from assets


    private var mapView: MapView? = null
    override fun onStart() {
        super.onStart()
        locationTracker =
            LocationTracker("my.action").setInterval(5000).setGps(true).setNetWork(true)
                .currentLocation(CurrentLocationReceiver(object : CurrentLocationListener {
                    override fun onPermissionDiened() {
                        locationTracker.stopLocationService(baseContext)
                    }

                    override fun onCurrentLocation(p0: Location?) {
                        //set location
                        mapView?.let {
                            zoomToCurentLocation(p0)
                            locationTracker.stopLocationService(this@MainActivity)

                            //set routes
                            initRoutes()
                        }
                    }
                }))
                .start(baseContext, this)
    }

    private fun initRoutes() {
        try {
            val kmlFilePath = getFileFromAssets(this, "routes.kml").absolutePath
            Log.e("LOG:", kmlFilePath)
            val kmlFile = File(kmlFilePath)
            Log.e("LOG:", kmlFile.exists().toString())
            if (kmlFile.exists()) {
                val kmlDocument = KmlDocument();
                kmlDocument.parseKMLFile(kmlFile)
                val kmlOverlay =
                    kmlDocument.mKmlRoot.buildOverlay(map.mapUtils.map, null, null, kmlDocument)

                map.mapUtils.map.overlays.add(kmlOverlay)
                map.invalidate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    @Throws(IOException::class)
    fun getFileFromAssets(context: Context, fileName: String): File =
        File(context.cacheDir, fileName)
            .also {
                it.outputStream()
                    .use { cache -> context.assets.open(fileName).use { it.copyTo(cache) } }
            }


    private fun zoomToCurentLocation(p0: Location?) {
        Log.e("latitude", p0?.latitude.toString())
        Log.e("longitude", p0?.longitude.toString())
        val geoPoint = GeoPoint(p0)
        offlineMapView.setInitialPositionAndZoom(
            geoPoint,
            25.0
        )
        val marker = Marker(mapView)
        marker.position = geoPoint
        marker.icon = applicationContext.getDrawable(R.drawable.center)

        marker.title = "Hello SaiGon"
        marker.showInfoWindow()
        mapView?.overlays?.add(marker)
        mapView?.invalidate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationTracker.onRequestPermission(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopLocationService(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //handle check permission first
//        checkPermissions()
        offlineMapView = findViewById(R.id.map)
        offlineMapView.init(this, this)

        btnOfflineMap.setOnClickListener {
            startActivity(Intent(this, OnlineMapTest::class.java))
        }
//        checkPermissions()
    }


    override fun onGeoPointRecieved(geoPoint: GeoPoint?) {
        Toast.makeText(this, geoPoint?.toDoubleString(), Toast.LENGTH_SHORT).show()
    }

    override fun mapLoadFailed(ex: String?) {
        Toast.makeText(this, "Error; ${ex}", Toast.LENGTH_SHORT).show()
    }

    override fun mapLoadSuccess(mapView: MapView?, mapUtils: MapUtils?) {

        this.mapView = mapView
    }
}
